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

    email: State<String>,
    prevWeekDays: State<List<String>>,
    postingDateItems: State<List<PostingChartItem>>,
    recentPostingItems: State<List<PostingItem>>,

    val onPageSwitch: (MainPage) -> Unit,
) {
    val page by page
    val email by email
    val recentPostingItems by recentPostingItems
}

@Composable
fun rememberMainContentState(
    screenState: MainScreenState,
    presenter: MainPresenter,
): MainContentState {
    val currentPage = presenter.page.collectAsStateWithLifecycle()
    val email = presenter.email.collectAsStateWithLifecycle()
    val prevWeekDays = presenter.prevWeekDays.collectAsStateWithLifecycle()
    val postingDateItems = presenter.postingDateItems.collectAsStateWithLifecycle()
    val recentPostingItems = presenter.recentPostingItems.collectAsStateWithLifecycle()

    return remember {
        MainContentState(
            pagerState = screenState.pagerState,
            page = currentPage,
            email = email,
            prevWeekDays = prevWeekDays,
            postingDateItems = postingDateItems,
            recentPostingItems = recentPostingItems,
            onPageSwitch = presenter::onPageSwitch,
        )
    }
}