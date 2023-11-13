package com.mj.blogger.ui.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.post.presenter.PostDetailPresenter
import com.mj.blogger.ui.post.presenter.state.PostDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), PostDetailPresenter {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    sealed interface PostDetailEvent {
        data class Modify(
            val postId: String,
            val title: String,
            val message: String,
            val images: List<Uri>,
        ) : PostDetailEvent

        object DeleteError : PostDetailEvent
        object DeleteComplete : PostDetailEvent
        object Back : PostDetailEvent
    }

    private val _configuration = MutableStateFlow<PostDetail?>(null)
    fun configure(data: PostDetail) {
        viewModelScope.launch {
            _configuration.emit(data)
        }
    }

    override val postImages = _configuration
        .filterNotNull()
        .map { it.images }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override val postItem = _configuration.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )

    private val _postDetailEvent = MutableSharedFlow<PostDetailEvent>()
    val postDetailEvent = _postDetailEvent.asSharedFlow()

    override fun onModify() {
        viewModelScope.launch {
            val item = _configuration.firstOrNull() ?: return@launch
            _postDetailEvent.emit(
                PostDetailEvent.Modify(
                    postId = item.postId,
                    title = item.title,
                    message = item.message,
                    images = item.images,
                )
            )
        }
    }

    override fun onDelete() {
        viewModelScope.launch {
            runCatching {
                val userId = repository.userIdFlow.firstOrNull() ?: return@launch
                val postId = _configuration.firstOrNull()?.postId ?: return@launch
                Log.d(TAG, "userId = $userId")
                Log.d(TAG, "postId = $postId")
                fireStore.collection(userId).document(postId).delete().await()
                val imagesResult = storage.reference.child("images/$postId").listAll().await()
                Log.d(TAG, "imageResult = $imagesResult")
                imagesResult.items.let { items ->
                    for (item in items) {
                        item.delete().await()
                    }
                }
            }.onSuccess {
                _postDetailEvent.emit(PostDetailEvent.DeleteComplete)
            }.onFailure { tr ->
                Log.w(TAG, tr)
                _postDetailEvent.emit(PostDetailEvent.DeleteError)
            }
        }
    }

    override fun onBack() {
        viewModelScope.launch {
            _postDetailEvent.emit(PostDetailEvent.Back)
        }
    }
}