package com.mj.blogger.ui.main.presentation.state

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.MainPresenter

@Stable
class MainContentState(
    data: State<String>,
    val close: () -> Unit,
) {
    val data by data
}

@Composable
fun rememberMainContentState(
    presenter: MainPresenter,
): MainContentState {
    val data = presenter.data.collectAsStateWithLifecycle()

    return remember {
        MainContentState(
            data = data,
            close = presenter::close
        )
    }
}