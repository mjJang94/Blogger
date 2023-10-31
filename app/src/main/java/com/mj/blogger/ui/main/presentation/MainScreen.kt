@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import com.github.mikephil.charting.data.BarEntry
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.PostingItem
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
    val pagerState = rememberPagerState(initialPage = initialPage.ordinal.coerceIn(pages.indices)){
        pages.size
    }

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

    val pages = Page.values()

    LaunchedEffect(state.page) {
        snapshotFlow {
            state.page.ordinal.coerceIn(pages.indices)
        }.collect {
            state.pagerState.scrollToPage(it)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = state.pagerState,
//            pageCount = pages.size,
            key = { it },
            userScrollEnabled = false,
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Page.HOME -> MainHomeContent(state = state)
                Page.BLOG -> MainBlogContent(state = state)
                Page.SETTING -> MainSettingsContent()
                else -> {}
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray,
        )

        BottomNavigator(
            modifier = Modifier.fillMaxWidth(),
            pages = pages,
            selectedPage = state.page,
            onPageSwitch = state.onPageSwitch,
        )
    }
}

@Composable
private fun BottomNavigator(
    modifier: Modifier,
    pages: Array<Page>,
    selectedPage: Page,
    onPageSwitch: (Page) -> Unit,
) {

    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedPage.ordinal,
        indicator = { TabRowDefaults.Indicator(color = Color.Transparent) },
    ) {
        pages.forEach { page ->
            Tab(
                selected = selectedPage == page,
                onClick = { onPageSwitch(page) },
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White),
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

@Preview
@Composable
private fun MainScreenPreview() {
    BloggerTheme {
        MainScreenContent(rememberPreviewMainContentState())
    }
}

@Composable
internal fun rememberPreviewMainContentState(): MainContentState {

    val prevWeekDays = listOf(
        "월",
        "화",
        "수",
        "목",
        "금",
        "토",
        "일",
    )

    val barEntry = listOf(
        BarEntry(0f, 1f),
        BarEntry(1f, 2f),
        BarEntry(2f, 3f),
        BarEntry(3f, 4f),
        BarEntry(4f, 5f),
        BarEntry(5f, 6f),
        BarEntry(6f, 7f),
    )

    val recentItems = listOf(
        PostingItem(
            postId = "",
            title = "안드로이드 활용법",
            message = "안드로이드 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 1,
        ),
        PostingItem(
            postId = "",
            title = "파이어베이스 활용법",
            message = "파이어베이스 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 2,
        ),
        PostingItem(
            postId = "",
            title = "갤럭시 활용법",
            message = "갤럭시에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 3,
        )

    )

    val state = MainContentState(
        pagerState = rememberPagerState(initialPage = Page.HOME.ordinal.coerceIn(Page.values().indices)){
            Page.values().size
        },
        page = remember { mutableStateOf(Page.HOME) },
        email = remember { mutableStateOf("alswhddl10@naver.com") },
        recentPostingItems = remember { mutableStateOf(recentItems) },
        allPostingItems = remember { mutableStateOf(recentItems) },
        onPageSwitch = {},
        openDetail = {},
    )

    return remember { state }
}