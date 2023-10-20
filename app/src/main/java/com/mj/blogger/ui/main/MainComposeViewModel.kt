package com.mj.blogger.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.ui.main.presentation.MainComposePresenter
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainComposeViewModel: ViewModel(), MainComposePresenter {

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
        Log.d(tag, "post()")
    }

    private val _closeEvent = MutableSharedFlow<Unit>()
    val closeEvent = _closeEvent.asSharedFlow()
    override fun onClose() {
        viewModelScope.launch { _closeEvent() }
    }
}