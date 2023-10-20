package com.mj.blogger.ui.main.presentation

import kotlinx.coroutines.flow.StateFlow

interface MainComposePresenter {
    val title: StateFlow<String>
    val message: StateFlow<String>

    fun onTitleChanged(insert: String)
    fun onMessageChanged(insert: String)
    fun onPost()
    fun onClose()
}