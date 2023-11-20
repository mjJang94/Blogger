@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.MainScreenState

@Stable
class MainContentState(
    val pagerState: PagerState,
    page: State<MainPage>,

    postingLoaded: State<Boolean>,
    email: State<String>,
    recentPostingItems: State<List<PostingItem>>,
    hitsPostingItems: State<List<PostingItem>>,
    allPostingItems: State<List<PostingItem>>,

    val pageSwitch: (MainPage) -> Unit,
    val openDetail: (PostingItem) -> Unit,
) {
    val page by page
    val postingLoaded by postingLoaded
    val email by email
    val recentPostingItems by recentPostingItems
    val hitsPostingItems by hitsPostingItems
    val allPostingItems by allPostingItems
}

@Composable
fun rememberMainContentState(
    screenState: MainScreenState,
    presenter: MainPresenter,
): MainContentState {
    val currentPage = presenter.page.collectAsStateWithLifecycle()
    val postingLoaded = presenter.postingLoaded.collectAsStateWithLifecycle()
    val email = presenter.email.collectAsStateWithLifecycle()
    val recentPostingItems = presenter.recentPostingItems.collectAsStateWithLifecycle()
    val hitsPostingItems = presenter.hitsPostingItems.collectAsStateWithLifecycle()
    val allPostingItems = presenter.allPostingItems.collectAsStateWithLifecycle()

    return remember {
        MainContentState(
            pagerState = screenState.pagerState,
            page = currentPage,
            postingLoaded = postingLoaded,
            email = email,
            recentPostingItems = recentPostingItems,
            hitsPostingItems = hitsPostingItems,
            allPostingItems = allPostingItems,
            pageSwitch = presenter::onPageSwitch,
            openDetail = presenter::openDetail,
        )
    }
}