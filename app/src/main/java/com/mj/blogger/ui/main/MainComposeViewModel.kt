package com.mj.blogger.ui.main

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
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
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainComposeViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val repository: Repository,
) : ViewModel(), MainComposePresenter {

    private val tag = this::class.java.simpleName

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

    override fun onPost() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull() ?: return@launch
            val title = _title.firstOrNull() ?: return@launch
            val message = _message.firstOrNull() ?: return@launch
            val images = _images.firstOrNull() ?: return@launch
            Log.d(tag, "onPost() : userId = $userId, title = $title, message = $message, images = $images")

            val post = Posting(
                title = title,
                message = message,
                postTime = System.currentTimeMillis(),
            )

            fireStore.collection(userId)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    val documentId = documentReference.id
                    Log.d(tag, "DocumentSnapshot added with ID: $documentId")
                    if (images.isEmpty()) {
                        complete()
                    } else {
                        uploadImage(documentId, images)
                    }
                }
                .addOnFailureListener { tr ->
                    Log.w(tag, "Error adding document", tr)
                }
        }
    }

    private fun uploadImage(documentId: String, images: List<Uri>) {
        var uploadCount = 0
        for ((index, imageUri) in images.withIndex()) {
            storage.reference.child("images/$documentId/image$index.jpg")
                .putFile(imageUri)
                .addOnSuccessListener {
                    uploadCount++
                }
                .addOnFailureListener { tr ->
                    Log.w(tag, "Error uploading image", tr)
                }
        }
        if (uploadCount == images.size) {
            complete()
        } else {
            Log.w(tag, "Some image not uploaded")
        }
    }

    private val _imageWithPosition = MutableStateFlow<Pair<Int, Uri>?>(null)
    override val imageWithPosition = _imageWithPosition.asStateFlow()

    private val _images = MutableStateFlow<List<Uri>>(emptyList())
    override val images = _images.asStateFlow()
    fun imagePicked(images: List<Uri>) {
        viewModelScope.launch {
            Log.d(tag, "imagePicked(): $images")

            val cursorPosition = _messageCursorPosition.firstOrNull() ?: 0
//            images.forEach { uri ->
//                _imageWithPosition.emit(cursorPosition to uri)
//            }

            val prevMessage = _message.firstOrNull()
            val nextText = StringBuilder(prevMessage).apply {
                images.forEach { uri ->
                    insert(cursorPosition, "\n + $uri")
                }
            }
            _message.emit(nextText.toString())
            _images.emit(images)
        }
    }

    private val _pickImageEvent = MutableSharedFlow<Unit>()
    val pickImageEvent = _pickImageEvent.asSharedFlow()
    override fun onPickImage() {
        viewModelScope.launch { _pickImageEvent() }
    }

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