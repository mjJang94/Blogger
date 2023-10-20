package com.mj.blogger.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.state.MainPage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(), MainPresenter {

    private val _page = MutableStateFlow(MainPage.HOME)
    override val page = _page.asStateFlow()
    override fun onPageSwitch(page: MainPage) {
        viewModelScope.launch {
            when (page) {
                MainPage.COMPOSE -> _composeEvent()
                else -> _page.emit(page)
            }
        }
    }


    private val _composeEvent = MutableSharedFlow<Unit>()
    val composeEvent = _composeEvent.asSharedFlow()
    override fun onComposePosting() {
        viewModelScope.launch { _composeEvent() }
    }
}