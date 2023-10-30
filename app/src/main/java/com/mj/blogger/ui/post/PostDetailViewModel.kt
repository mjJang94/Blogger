package com.mj.blogger.ui.post

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.post.presenter.PostDetailPresenter
import com.mj.blogger.ui.post.presenter.state.PostDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val repository: Repository,
) : ViewModel(), PostDetailPresenter {

    companion object {
        private val TAG = this::class.java.simpleName
    }

    private val _postId = MutableStateFlow("")
    fun configure(postId: String) {
        viewModelScope.launch {
            _postId.emit(postId)
        }
    }

    class PostDocumentEmptyException : Exception()

    val post = combine(
        repository.userIdFlow,
        _postId
    ) { userId, postId ->
        runCatching {
            val document = fireStore.collection(userId).document(postId).get().await()
            document.toObject<Posting>()?.translate() ?: throw PostDocumentEmptyException()
        }.onFailure { tr ->
            Log.e(TAG, "$tr")
            _loadErrorEvent.emit(tr)
        }.getOrNull()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    private fun Posting.translate() = PostDetail(
        title = this.title,
        message = this.message,
        postTime = this.postTime,
    )

    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()
}