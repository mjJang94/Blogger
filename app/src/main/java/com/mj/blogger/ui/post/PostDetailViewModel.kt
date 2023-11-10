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

    private val _configuration = MutableStateFlow<PostDetail?>(null)
    fun configure(data: PostDetail) {
        viewModelScope.launch {
            _configuration.emit(data)
        }
    }

    override val postItem = _configuration.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null,
    )
//    override val postItem = combine(
//        repository.userIdFlow,
//        _configuration
//    ) { userId, item ->
//        runCatching {
//            val postId = item? ?: throw ConfigurationEmptyException()
//            //load from fire-store
//            val document = fireStore.collection(userId).document(postId).get().await()
//            //load from storage
//            val storageResult = storage.reference.child("images/$postId").listAll().await()
//            //collect all images from storage
//            val images = storageResult.items.map { imageRef ->
//                imageRef.downloadUrl.await()
//            }
//            //combine data
//            document.toObject<Posting>()?.translate(images) ?: throw PostDocumentEmptyException()
//        }.onFailure { tr ->
//            Log.e(TAG, "$tr")
//            _loadErrorEvent.emit(tr)
//        }.getOrNull()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.Lazily,
//        initialValue = null
//    )

    private fun Posting.translate(images: List<Uri>) = PostDetail(
        title = title,
        message = message,
        postTime = postTime,
        images = images
    )

    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()

    private val _backEvent = MutableSharedFlow<Unit>()
    val backEvent = _backEvent.asSharedFlow()
    override fun onBack() {
        viewModelScope.launch {
            _backEvent()
        }
    }
}