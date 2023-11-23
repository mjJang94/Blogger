package com.mj.blogger.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.state.MainPage
import com.mj.blogger.ui.main.presentation.state.PostingItem
import com.mj.blogger.ui.post.presentation.state.PostDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import com.mj.blogger.ui.main.MainViewModel.UserEvent as Event

@HiltViewModel
class MainViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), MainPresenter {

    companion object {
        private const val MAXIMUM_LAST_POST_COUNT = 10
        private const val MAXIMUM_HITS_POST_COUNT = 3
    }

    enum class UserEvent {
        INVALIDATE, LOGOUT, RESIGN,
    }

    init {
        fetchPostingData()
    }

    private val _postingLoaded = MutableStateFlow(false)
    override val postingLoaded = _postingLoaded.asStateFlow()

    private val _page = MutableStateFlow(MainPage.HOME)
    override val page = _page.asStateFlow()
    override fun onPageSwitch(page: MainPage) {
        viewModelScope.launch {
            when (page) {
                MainPage.COMPOSE -> _composeEvent()
                else -> _page.emit(page)
            }
        }
    }

    override val email = repository.emailFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = "",
    )

    fun fetchPostingData() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull()
                ?: return@launch _invalidateEvent.emit(Event.INVALIDATE)
            fireStore.collection(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    combinePostingItems(snapshot)
                }
                .addOnFailureListener { exception ->
                    Timber.e("$exception")
                    when (exception) {
                        is FirebaseFirestoreException -> {
                            if (FirebaseFirestoreException.Code.PERMISSION_DENIED == exception.code) {
                                logout()
                            }
                        }
                    }
                }
        }
    }

    private val _postingItems = MutableStateFlow<List<PostingItem>>(emptyList())
    private fun combinePostingItems(snapshot: QuerySnapshot?) {
        viewModelScope.launch {

            val postings = snapshot?.toObjects(Posting::class.java) ?: emptyList()
            val combineContents = postings
                .sortedBy { it.postTime }
                .map {
                    val images = withContext(Dispatchers.IO) {
                        val imageRef = storage.reference.child("images/").child(it.postId).listAll().await()
                        imageRef.items.map { ref -> ref.downloadUrl.await() }
                    }
                    Timber.d("combinePostingItems = $images")
                    it.translate(images)
                }
            _postingItems.emit(combineContents)
            _postingLoaded.emit(true)
        }
    }

    private fun Posting.translate(images: List<Uri>) = PostingItem(
        postId = postId,
        title = title,
        message = message,
        postTime = postTime,
        thumbnail = images.firstOrNull(),
        hits = hits,
        images = images,
    )

    override val recentPostingItems = _postingItems
        .map { it.reversed().take(MAXIMUM_LAST_POST_COUNT) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override val hitsPostingItems = _postingItems
        .map { it.sortByHits() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override val allPostingItems = _postingItems
        .map { it.reversed() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    private fun List<PostingItem>.sortByHits(): List<PostingItem> =
        this.sortedWith(
            compareByDescending<PostingItem> { it.hits }.thenByDescending { it.postTime }
        ).take(MAXIMUM_HITS_POST_COUNT)

    private val _composeEvent = MutableSharedFlow<Unit>()
    val composeEvent = _composeEvent.asSharedFlow()

    private val _openDetailEvent = MutableSharedFlow<PostDetail>()
    val openDetailEvent = _openDetailEvent.asSharedFlow()
    override fun openDetail(item: PostingItem) {
        viewModelScope.launch {
            val data = PostDetail(
                postId = item.postId,
                title = item.title,
                message = item.message,
                postTime = item.postTime,
                hits = item.hits,
                images = item.images,
            )
            _openDetailEvent.emit(data)
        }
    }

    private val _invalidateEvent = MutableSharedFlow<Event>()
    val invalidateEvent = _invalidateEvent.asSharedFlow()
    override fun logout() {
        clearDataStore {
            auth.signOut()
            _invalidateEvent.emit(Event.LOGOUT)
        }
    }

    override fun resign() {
        auth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                clearDataStore {
                    _invalidateEvent.emit(Event.RESIGN)
                }
            }
        }
    }

    private fun clearDataStore(action: suspend () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.clearAll()
            action()
        }
    }
}