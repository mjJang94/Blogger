package com.mj.blogger.ui.main

import androidx.lifecycle.ViewModel
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.state.MainPage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel: ViewModel(), MainPresenter {

    private val _page = MutableStateFlow(MainPage.HOME)
    override val page = _page.asStateFlow()

    override fun close() {

    }
}