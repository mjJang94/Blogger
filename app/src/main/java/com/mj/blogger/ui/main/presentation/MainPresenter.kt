package com.mj.blogger.ui.main.presentation

import kotlinx.coroutines.flow.StateFlow

interface MainPresenter {
    val data: StateFlow<String>
    fun close()
}