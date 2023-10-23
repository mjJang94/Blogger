package com.mj.blogger.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainComposePresenter
import com.mj.blogger.ui.main.presentation.PostingData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainComposeViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
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

    private val _message = MutableStateFlow("")
    override val message: StateFlow<String> = _message.asStateFlow()
    override fun onMessageChanged(insert: String) {
        viewModelScope.launch {
            _message.emit(insert)
        }
    }

    override fun onPost() {
        viewModelScope.launch {
            val userId = repository.userIdFlow.firstOrNull() ?: return@launch
            val title = _title.firstOrNull() ?: return@launch
            val message = _message.firstOrNull() ?: return@launch
            Log.d(tag, "onPost() : userId = $userId, title = $title, message = $message")

            val post = PostingData(
                title = title,
                message = message,
                postTime = System.currentTimeMillis(),
            )

            fireStore.collection(userId)
                .add(post)
                .addOnSuccessListener { documentReference ->
                    Log.d(tag, "DocumentSnapshot added with ID: ${documentReference.id}")
                    complete()
                }
                .addOnFailureListener { tr ->
                    Log.w(tag, "Error adding document", tr)
                }
        }
    }

    private val _completeEvent = MutableSharedFlow<Unit>()
    val completeEvent = _completeEvent.asSharedFlow()
    private fun complete(){
        viewModelScope.launch { _completeEvent() }
    }

    private val _closeEvent = MutableSharedFlow<Unit>()
    val closeEvent = _closeEvent.asSharedFlow()
    override fun onClose() {
        viewModelScope.launch { _closeEvent() }
    }
}