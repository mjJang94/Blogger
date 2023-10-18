@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.rememberMainContentState
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

@Stable
class MainScreenState constructor(
    val pagerState: PagerState,
)

@Composable
fun rememberMainScreenState(
    initialPage: Page = Page.HOME,
): MainScreenState {
    val pages = Page.values()
    val pagerState = rememberPagerState(initialPage = initialPage.ordinal.coerceIn(pages.indices))

    return MainScreenState(
        pagerState = pagerState
    )
}

@Composable
fun MainScreen(
    presenter: MainPresenter,
    state: MainScreenState = rememberMainScreenState(),
) {
    MainScreenContent(
        rememberMainContentState(
            screenState = state,
            presenter = presenter,
        )
    )
}

@Composable
private fun MainScreenContent(state: MainContentState) {

    var selectedPage by remember { mutableStateOf(Page.HOME) }

    val pages = Page.values()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = state.pagerState,
            pageCount = pages.size,
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Page.HOME -> MainHomeContent()
                Page.CREATE -> MainCreateContent()
                Page.SETTINGS -> MainSettingsContent()
            }
        }

        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedPage.ordinal,
            indicator = { TabRowDefaults.Indicator(color = Color.Transparent) },
        ) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = selectedPage == page,
                    onClick = { selectedPage = pages[index] },
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(
                                id = when (selectedPage == page) {
                                    true -> page.selectedIconRes
                                    false -> page.defaultIconRes
                                }
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val state = MainContentState(
        pagerState = rememberPagerState(initialPage = Page.HOME.ordinal),
        page = remember { mutableStateOf(Page.HOME) },
        close = {}
    )
    BloggerTheme {
        MainScreenContent(state)
    }
}