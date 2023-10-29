@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation.state

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.mikephil.charting.data.BarEntry
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.MainScreenState

@Stable
class MainContentState(
    val pagerState: PagerState,
    page: State<MainPage>,

    email: State<String>,
    prevWeekDays: State<List<String>>,
    postingChartEntryItems: State<List<BarEntry>>,
    recentPostingItems: State<List<PostingItem>>,
    allPostingItems: State<List<PostingItem>>,

    val onPageSwitch: (MainPage) -> Unit,
    val openDetail: (String) -> Unit,
) {
    val page by page
    val email by email
    val prevWeekDays by prevWeekDays
    val postingChartEntryItems by postingChartEntryItems
    val recentPostingItems by recentPostingItems
    val allPostingItems by allPostingItems
}

@Composable
fun rememberMainContentState(
    screenState: MainScreenState,
    presenter: MainPresenter,
): MainContentState {
    val currentPage = presenter.page.collectAsStateWithLifecycle()
    val email = presenter.email.collectAsStateWithLifecycle()
    val prevWeekDays = presenter.prevWeekDays.collectAsStateWithLifecycle()
    val postingChartEntryItems = presenter.postingChartEntryItems.collectAsStateWithLifecycle()
    val recentPostingItems = presenter.recentPostingItems.collectAsStateWithLifecycle()
    val allPostingItems = presenter.allPostingItems.collectAsStateWithLifecycle()

    return remember {
        MainContentState(
            pagerState = screenState.pagerState,
            page = currentPage,
            email = email,
            prevWeekDays = prevWeekDays,
            postingChartEntryItems = postingChartEntryItems,
            recentPostingItems = recentPostingItems,
            allPostingItems = allPostingItems,
            onPageSwitch = presenter::onPageSwitch,
            openDetail = presenter::openDetail,
        )
    }
}