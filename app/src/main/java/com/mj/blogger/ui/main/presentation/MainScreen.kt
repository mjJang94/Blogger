@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mj.blogger.common.compose.foundation.BloggerImage
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.MainPages
import com.mj.blogger.ui.main.presentation.state.rememberMainContentState

@Composable
fun MainScreen(presenter: MainPresenter) {
    MainScreenContent(rememberMainContentState(presenter = presenter))
}

@Composable
private fun MainScreenContent(state: MainContentState) {

    var selectedPage by remember { mutableStateOf(MainPages.HOME) }
    val pagerState = rememberPagerState(initialPage = MainPages.HOME.ordinal)

    val pages = MainPages.values()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            pageCount = pages.size,
            state = pagerState,
        ) { page ->
            when (page) {
                MainPages.HOME.ordinal -> MainHomeContent()
                MainPages.CREATE.ordinal -> MainCreateContent()
                MainPages.SETTINGS.ordinal -> MainSettingsContent()

            }
        }

        TabRow(
            modifier = Modifier.fillMaxWidth(),
            selectedTabIndex = selectedPage.ordinal,
            indicator = { Spacer(modifier = Modifier.background(Color.Transparent))},
        ){
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = selectedPage == page,
                    onClick = { selectedPage = pages[index] },
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Color.Gray,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = stringResource(id = page.title)
                            )

                            BloggerImage(
                                painter = painterResource(id = page.iconRes)
                            )
                        }
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
        data = remember { mutableStateOf("") },
        close = {}
    )
    BloggerTheme {
        MainScreenContent(state)
    }
}