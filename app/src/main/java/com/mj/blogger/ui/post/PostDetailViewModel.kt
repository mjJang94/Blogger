package com.mj.blogger.ui.post

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.common.base.ConfigurationEmptyException
import com.mj.blogger.common.base.PostDocumentEmptyException
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.state.PostingItem
import com.mj.blogger.ui.post.presenter.PostDetailPresenter
import com.mj.blogger.ui.post.presenter.state.PostDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val _userId = repository.userIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = "",
    )

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

    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()

    private val _deleteErrorEvent = MutableSharedFlow<Throwable>()
    val deleteErrorEvent = _deleteErrorEvent.asSharedFlow()

    private val _modifyEvent = MutableSharedFlow<Unit>()
    val modifyEvent = _modifyEvent.asSharedFlow()
    override fun onModify() {
        viewModelScope.launch {
            _modifyEvent()
        }
    }

    private val _deleteEvent = MutableSharedFlow<Unit>()
    val deleteEvent = _deleteEvent.asSharedFlow()
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
                    for(item in items){
                        item.delete().await()
                    }
                }
            }.onSuccess {
                _deleteEvent()
            }.onFailure { tr ->
                Log.w(TAG, tr)
                _deleteErrorEvent.emit(tr)
            }
        }
    }

    private val _backEvent = MutableSharedFlow<Unit>()
    val backEvent = _backEvent.asSharedFlow()
    override fun onBack() {
        viewModelScope.launch {
            _backEvent()
        }
    }
}