package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.rememberMainContentState

@Composable
fun MainScreen(presenter: MainPresenter) {
    MainScreenContent(rememberMainContentState(presenter = presenter))
}

@Composable
private fun MainScreenContent(state: MainContentState) {
    Column() {

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