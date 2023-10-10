package com.mj.blogger.ui.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.blogger.ui.login.presentation.LoginPresenter
import com.mj.blogger.ui.login.presentation.SignInfo
import com.mj.blogger.ui.login.presentation.SignType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel(), LoginPresenter {

    private val _email = MutableStateFlow("")
    override val email: StateFlow<String> = _email.asStateFlow()
    override fun onEmailChanged(insert: String) {
        viewModelScope.launch {
            Log.d("VMLogin", "id insert = $insert")
            _email.emit(insert)
        }
    }

    private val _password = MutableStateFlow("")
    override val password: StateFlow<String> = _password.asStateFlow()
    override fun onPasswordChanged(insert: String) {
        viewModelScope.launch {
            Log.d("VMLogin", "password insert = $insert")
            _password.emit(insert)
        }
    }

    override val enabled: StateFlow<Boolean> = combine(_email, _password) { email, password ->
        when {
            email.isBlank() || password.isBlank() -> true
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> true
            else -> false
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = true,
    )

    private val _signEvent = MutableSharedFlow<SignInfo>()
    val signEvent = _signEvent.asSharedFlow()
    override fun onSign(type: SignType) {
        viewModelScope.launch {
            val id = _email.firstOrNull() ?: return@launch
            val password = _password.firstOrNull() ?: return@launch
            val info = SignInfo(
                requestType = type,
                id = id,
                password = password,
            )
            _signEvent.emit(info)
        }
    }
}