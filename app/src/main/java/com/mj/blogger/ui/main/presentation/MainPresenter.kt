package com.mj.blogger.ui.main.presentation

import kotlinx.coroutines.flow.StateFlow
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

interface MainPresenter {
    val page: StateFlow<Page>
    fun close()
}