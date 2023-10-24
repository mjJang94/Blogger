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

    val onPageSwitch: (MainPage) -> Unit,
) {
    val page by page
}

@Composable
fun rememberMainContentState(
    screenState: MainScreenState,
    presenter: MainPresenter,
): MainContentState {
    val currentPage = presenter.page.collectAsStateWithLifecycle()

    return remember {
        MainContentState(
            pagerState = screenState.pagerState,
            page = currentPage,
            onPageSwitch = presenter::onPageSwitch,
        )
    }
}