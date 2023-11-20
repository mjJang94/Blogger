package com.mj.blogger.ui.main

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), MainPresenter {

    companion object {
        private const val MAXIMUM_LAST_POST_COUNT = 10
        private const val MAXIMUM_HITS_POST_COUNT = 5
    }

    class InvalidUserException : Exception()

    init {
        viewModelScope.launch {
            runCatching {
                val userId = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }
                fireStore.collection(userId)
                    .orderBy("postTime")
                    .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, exception ->
                        when {
                            exception != null -> {
                                Timber.e("$exception")
                                return@addSnapshotListener loadError(exception)
                            }

                            else -> {
                                combinePostingItems(snapshot)
                            }
                        }
                    }
            }.getOrElse { tr ->
                Timber.e("Snapshot load failure : $tr")
                loadError(tr)
            }
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

    private val _postingItems = MutableStateFlow<List<PostingItem>>(emptyList())
    private fun combinePostingItems(snapshot: QuerySnapshot?) {
        viewModelScope.launch {
            val postings = snapshot?.toObjects<Posting>() ?: emptyList()
            val combineContents = postings.map {
                val imageRef = storage.reference.child("images/${it.postId}").listAll().await()
                val images = imageRef.items.map { ref -> ref.downloadUrl.await() }
                it.translate(images)
            }
            _postingItems.emit(combineContents)
            _postingLoaded.emit(true)
        }
    }

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


    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()
    private fun loadError(tr: Throwable) {
        viewModelScope.launch {
            _loadErrorEvent.emit(tr)
        }
    }

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
}