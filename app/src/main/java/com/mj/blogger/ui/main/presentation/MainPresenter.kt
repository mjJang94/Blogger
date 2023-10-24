package com.mj.blogger.ui.main.presentation

import com.mj.blogger.ui.main.presentation.state.MainPage
import kotlinx.coroutines.flow.StateFlow
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

interface MainPresenter {
    val page: StateFlow<Page>

    val postingItems : StateFlow<List<PostingData>>

    fun onPageSwitch(page: MainPage)
}