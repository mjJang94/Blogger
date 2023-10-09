package com.mj.blogger.ui.login.presentation

data class SignInfo(
    val requestType: SignType,
    val id: String,
    val password: String
)