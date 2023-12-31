@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationGraphicsApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    val pagerState = rememberPagerState(initialPage = initialPage.ordinal.coerceIn(pages.indices)) {
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
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            state = state.pagerState,
            key = { it },
            userScrollEnabled = false,
        ) { pageIndex ->
            when (pages[pageIndex]) {
                Page.HOME -> MainHomeContent(state = state)
                Page.BLOG -> MainBlogContent(state = state)
                Page.SETTING -> MainSettingsContent(state = state)
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
            onPageSwitch = state.pageSwitch,
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
                interactionSource = remember { MutableInteractionSource() }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = rememberAnimatedVectorPainter(
                            animatedImageVector = AnimatedImageVector.animatedVectorResource(
                                id = page.animationRes,
                            ),
                            atEnd = selectedPage == page
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
    val recentItems = listOf(
        PostingItem(
            postId = "1",
            title = "안드로이드 활용법",
            message = "안드로이드 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 1,
            thumbnail = null,
            hits = 99,
            images = emptyList(),
        ),
        PostingItem(
            postId = "2",
            title = "파이어베이스 활용법",
            message = "파이어베이스 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 2,
            thumbnail = null,
            hits = 2,
            images = emptyList(),
        ),
        PostingItem(
            postId = "3",
            title = "갤럭시 활용법",
            message = "갤럭시에 대해 알아봅니다.",
            postTime = System.currentTimeMillis() + 3,
            thumbnail = null,
            hits = 9,
            images = emptyList(),
        )

    )

    val state = MainContentState(
        pagerState = rememberPagerState(initialPage = Page.HOME.ordinal.coerceIn(Page.values().indices)) {
            Page.values().size
        },
        page = remember { mutableStateOf(Page.HOME) },
        fetchPosting = remember { mutableStateOf(false) },
        postingLoaded = remember { mutableStateOf(true) },
        email = remember { mutableStateOf("alswhddl10@naver.com") },
        recentPostingItems = remember { mutableStateOf(emptyList()) },
        hitsPostingItems = remember { mutableStateOf(emptyList()) },
        allPostingItems = remember { mutableStateOf(emptyList()) },
        pageSwitch = {},
        openDetail = {},
        logout = {},
        resign = {},
    )

    return remember { state }
}