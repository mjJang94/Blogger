package com.mj.blogger.ui.main.presentation

import com.mj.blogger.ui.main.presentation.state.MainPage
import com.mj.blogger.ui.main.presentation.state.PostingItem
import kotlinx.coroutines.flow.StateFlow
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

interface MainPresenter {
    val page: StateFlow<Page>

    val postingLoaded: StateFlow<Boolean>
    val fetchPosting: StateFlow<Boolean>
    val email: StateFlow<String>

    val recentPostingItems : StateFlow<List<PostingItem>>
    val hitsPostingItems : StateFlow<List<PostingItem>>
    val allPostingItems : StateFlow<List<PostingItem>>

    fun onPageSwitch(page: MainPage)
    fun openDetail(item: PostingItem)
    fun logout()
    fun resign()
}