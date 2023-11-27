package com.mj.blogger.ui.compose

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mj.blogger.common.base.ImageUploadFailException
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.common.ktx.context
import com.mj.blogger.core.UploadHelper.compressImage
import com.mj.blogger.core.UploadHelper.downloadAndConvertToInternalUri
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.compose.presentation.ComposePresenter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ComposeViewModel @Inject constructor(
    application: Application,
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : AndroidViewModel(application), ComposePresenter {

    companion object {
        private const val MAX_IMAGE_COUNT = 3
    }

    private val _isModify = MutableStateFlow(false)
    override val isModify = _isModify.asStateFlow()
    fun configure(
        postId: String,
        title: String,
        message: String,
        hits: Int,
        images: List<Uri>,
    ) {
        viewModelScope.launch {
            _isModify.emit(true)
            _postId.emit(postId)
            _title.emit(title)
            _message.emit(message)
            _hits.emit(hits)
            _images.emit(images)
        }
    }

    private val _postId = MutableStateFlow("")
    private val _hits = MutableStateFlow(0)

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
            Timber.d("imagePicked(): $images")
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

    private val _progressing = MutableStateFlow(false)
    override val progressing = _progressing.asStateFlow()

    override fun onPost() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull() ?: return@launch
            val title = _title.firstOrNull() ?: return@launch
            val message = _message.firstOrNull() ?: return@launch
            val images = _images.firstOrNull() ?: return@launch
            Timber.d("onPost() : userId = $userId, title = $title, message = $message, images = $images")

            _progressing.emit(true)

            val post = Posting(
                title = title,
                message = message,
                postTime = System.currentTimeMillis(),
            )

            fireStore.collection(userId)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    when (images.isEmpty()) {
                        true -> complete()
                        else -> uploadImage(documentReference.id, images)
                    }
                }
                .addOnFailureListener { tr ->
                    Timber.w("Error uploading document : $tr")
                    uploadFail(tr)
                }
        }
    }

    private fun uploadImage(postId: String, images: List<Uri>) {
        viewModelScope.launch {
            runCatching {
                launch(Dispatchers.IO) {
                    images.forEachIndexed { index, uri ->
                        val compress = compressImage(context, uri, 500, 300)
                        storage.reference.child("images/$postId/image$index.jpg").putFile(compress).await()
                    }
                }.join()
            }.onSuccess {
                complete()
            }.onFailure { tr ->
                Timber.w("Upload failure : $tr")
                uploadFail(ImageUploadFailException())
            }
        }
    }

    override fun onModify() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull() ?: return@launch
            val postId = _postId.firstOrNull() ?: return@launch
            val title = _title.firstOrNull() ?: return@launch
            val message = _message.firstOrNull() ?: return@launch
            val hits = _hits.firstOrNull() ?: return@launch
            val images = _images.firstOrNull() ?: return@launch

            _progressing.emit(true)

            val post = Posting(
                postId = postId,
                title = title,
                message = message,
                postTime = System.currentTimeMillis(),
                hits = hits,
            )

            fireStore.collection(userId)
                .document(postId)
                .set(post)
                .addOnSuccessListener { _ ->
                    when (images.isEmpty()) {
                        true -> complete()
                        else -> modifyImage(postId, images)
                    }
                }
                .addOnFailureListener { tr ->
                    Timber.w("Error uploading document : $tr")
                    uploadFail(tr)
                }
        }
    }

    private fun modifyImage(postId: String, images: List<Uri>) {
        viewModelScope.launch {
            runCatching {
                launch(Dispatchers.IO) {
                    images.forEachIndexed { index, uri ->
                        val image = when (uri.toString().startsWith("https")) {
                            true -> context.downloadAndConvertToInternalUri(uri.toString())
                            else -> uri
                        }
                        storage.reference.child("images/$postId/image_$index.jpg").putFile(image).await()
                    }
                }.join()
            }.onSuccess {
                complete()
            }.onFailure { tr ->
                Timber.w("Fail to modify images = $tr")
                uploadFail(ImageUploadFailException())
            }
        }
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
            Timber.d("images size = ${images?.size}")
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

    private val _maxImageEvent = MutableSharedFlow<Unit>()
    val maxImageEvent = _maxImageEvent.asSharedFlow()

    private val _uploadFailEvent = MutableSharedFlow<Throwable>()
    val uploadFailEvent = _uploadFailEvent.asSharedFlow()
    private fun uploadFail(tr: Throwable) {
        viewModelScope.launch {
            _progressing.emit(false)
            _uploadFailEvent.emit(tr)
        }
    }

    private val _completeEvent = MutableSharedFlow<Unit>()
    val completeEvent = _completeEvent.asSharedFlow()
    private fun complete() {
        viewModelScope.launch {
            _progressing.emit(false)
            _completeEvent()
        }
    }

    private val _closeEvent = MutableSharedFlow<Unit>()
    val closeEvent = _closeEvent.asSharedFlow()
    override fun onClose() {
        viewModelScope.launch { _closeEvent() }
    }
}