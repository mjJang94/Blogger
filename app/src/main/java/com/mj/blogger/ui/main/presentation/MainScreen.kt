package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

    var selectedPageIndex by remember { mutableStateOf(0) }
    val pages = MainPages.values()

    Column {
        TabRow(selectedTabIndex = selectedPageIndex) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = selectedPageIndex == index,
                    onClick = { selectedPageIndex = index },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
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
            when (selectedPageIndex) {
                MainPages.HOME.ordinal -> MainHomeContent()
                MainPages.CREATE.ordinal -> MainCreateContent()
                MainPages.SETTINGS.ordinal -> MainSettingsContent()
            }
        }
    }
}

@Preview
@Composable
fun GreetingPreview() {
    val state = MainContentState(
        data = remember { mutableStateOf("") },
        close = {}
    )
    BloggerTheme {
        MainScreenContent(state)
    }
}