package com.mj.blogger.ui.main.presentation

import com.mj.blogger.ui.main.presentation.state.MainPage
import kotlinx.coroutines.flow.StateFlow
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

interface MainPresenter {
    val page: StateFlow<Page>

    fun onPageSwitch(page: MainPage)
    fun onComposePosting()
}