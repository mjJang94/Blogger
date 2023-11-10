package com.mj.blogger.ui.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObjects
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.state.MainPage
import com.mj.blogger.ui.main.presentation.state.PostingItem
import com.mj.blogger.ui.post.presenter.state.PostDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), MainPresenter {

    companion object {
        private val TAG = this::class.java.simpleName
        private const val MAXIMUM_LAST_POST_COUNT = 10
    }

    class InvalidUserException : Exception()

    init {
        viewModelScope.launch {
            runCatching {
                val userId = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }
                fireStore.collection(userId).document()
                fireStore.collection(userId)
                    .orderBy("postTime")
                    .addSnapshotListener { documents, exception ->
                        when {
                            exception != null -> {
                                Log.e(TAG, "$exception")
                                loadError(exception)
                                return@addSnapshotListener
                            }

                            else -> {
                                combinePostingItems(documents)
                            }
                        }
                    }
            }.getOrElse { tr ->
                Log.e(TAG, "$tr")
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
    private fun combinePostingItems(documents: QuerySnapshot?) {
        viewModelScope.launch {
            val postings = documents?.toObjects<Posting>() ?: emptyList()
            Log.d(TAG, "postings = $postings")
            val combineContents = postings.map {
                val imageRef = storage.reference.child("images/${it.postId}").listAll().await()
                val images = imageRef.items.map { ref -> ref.downloadUrl.await() }
                Log.d(TAG, "posting images = $images")
                it.translate(images)
            }
            _postingItems.emit(combineContents)
            _postingLoaded.emit(true)
        }
    }

    override val recentPostingItems = _postingItems
        .take(MAXIMUM_LAST_POST_COUNT)
        .map { it.reversed() }
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
                title = item.title,
                message = item.message,
                postTime = item.postTime,
                images = item.images,
            )
            _openDetailEvent.emit(data)
        }
    }
}