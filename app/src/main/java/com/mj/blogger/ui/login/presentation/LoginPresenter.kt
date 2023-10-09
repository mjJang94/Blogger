package com.mj.blogger.ui.login.presentation

import kotlinx.coroutines.flow.StateFlow

interface LoginPresenter {
    val email: StateFlow<String>
    val password: StateFlow<String>
    val enabled: StateFlow<Boolean>

    fun onEmailChanged(insert: String)
    fun onPasswordChanged(insert: String)
    fun onSign(type: SignType)
}