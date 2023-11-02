package com.mj.blogger.ui.main

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainComposePresenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainComposeViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), MainComposePresenter {

    companion object {
        private val TAG = this::class.java.simpleName
        private const val MAX_IMAGE_COUNT = 3
    }

    private val _title = MutableStateFlow("")
    override val title: StateFlow<String> = _title.asStateFlow()
    override fun onTitleChanged(insert: String) {
        viewModelScope.launch {
            _title.emit(insert)
        }
    }

    private val _messageCursorPosition = MutableStateFlow(0)
    private val _message = MutableStateFlow("")
    override val message: StateFlow<String> = _message.asStateFlow()
    override fun onMessageChanged(insert: String) {
        viewModelScope.launch {
            _messageCursorPosition.emit(insert.length)
            _message.emit(insert)
        }
    }

    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    override val images = _images.asStateFlow()
    fun imagePicked(images: List<Uri>) {
        viewModelScope.launch {
            Log.d(TAG, "imagePicked(): $images")
            when (images.size <= MAX_IMAGE_COUNT) {
                true -> {
                    val lastImages = _images.firstOrNull() ?: emptyList()
                    val tmp = lastImages.toMutableList()
                    for (uri in images) {
                        tmp.add(uri)
                    }
                    _images.emit(tmp)
                }
                else -> _maxImageEvent()
            }
        }
    }

    override fun onPost() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull() ?: return@launch
            val title = _title.firstOrNull() ?: return@launch
            val message = _message.firstOrNull() ?: return@launch
            val images = _images.firstOrNull() ?: return@launch
            Log.d(TAG, "onPost() : userId = $userId, title = $title, message = $message, images = $images")

            val post = Posting(
                title = title,
                message = message,
                postTime = System.currentTimeMillis(),
            )

            fireStore.collection(userId)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    val postId = documentReference.id
                    Log.d(TAG, "document uploaded: $postId")
                    if (images.isEmpty()) {
                        complete()
                    } else {
                        uploadImage(postId, images)
                    }
                }
                .addOnFailureListener { tr ->
                    Log.w(TAG, "Error uploading document", tr)
                    uploadFail(tr)
                }
        }
    }

    class ImageUploadFailException : Exception()

    private fun uploadImage(postId: String, images: List<Uri>) {
        for ((index, imageUri) in images.withIndex()) {
            storage.reference.child("images/$postId/image$index.jpg")
                .putFile(imageUri)
                .addOnSuccessListener { snapShot ->
                    Log.w(TAG, "upload complete = $index, size = ${snapShot.totalByteCount}, uri = ${snapShot.uploadSessionUri}")
                }
                .addOnFailureListener { tr ->
                    Log.w(TAG, "Error uploading image", tr)
                    uploadFail(ImageUploadFailException())
                }
        }
        complete()
    }

    override val imagesCount = _images.map {
        it.size
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 0,
    )

    private val _pickImageEvent = MutableSharedFlow<Unit>()
    val pickImageEvent = _pickImageEvent.asSharedFlow()
    override fun onPickImage() {
        viewModelScope.launch {
            val images = _images.firstOrNull()
            when {
                images == null || images.size < MAX_IMAGE_COUNT -> _pickImageEvent()
                else -> _maxImageEvent()
            }
        }
    }

    override fun onImageCancel(index: Int) {
        viewModelScope.launch {
            val lastImages = _images.firstOrNull() ?: emptyList()
            val removeImages = lastImages.toMutableList().apply {
                removeAt(index)
            }
            _images.emit(removeImages)
        }
    }

    private val _uploadFailEvent = MutableSharedFlow<Throwable>()
    val uploadFailEvent = _uploadFailEvent.asSharedFlow()
    private fun uploadFail(tr: Throwable) {
        viewModelScope.launch { _uploadFailEvent.emit(tr) }
    }

    private val _maxImageEvent = MutableSharedFlow<Unit>()
    val maxImageEvent = _maxImageEvent.asSharedFlow()

    private val _completeEvent = MutableSharedFlow<Unit>()
    val completeEvent = _completeEvent.asSharedFlow()
    private fun complete() {
        viewModelScope.launch { _completeEvent() }
    }

    private val _closeEvent = MutableSharedFlow<Unit>()
    val closeEvent = _closeEvent.asSharedFlow()
    override fun onClose() {
        viewModelScope.launch { _closeEvent() }
    }
}