package com.mj.blogger.ui.main.presentation.state

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.MainComposePresenter

@Stable
class MainComposeState(
    title: State<String>,
    message: State<String>,

    val onTitleChanged: (String) -> Unit,
    val onMessageChanged: (String) -> Unit,
    val onPost: () -> Unit,
    val onClose: () -> Unit,
) {
    val title by title
    val message by message
}

@Composable
fun rememberMainComposeState(
    presenter: MainComposePresenter,
): MainComposeState {

    val title = presenter.title.collectAsStateWithLifecycle()
    val message = presenter.message.collectAsStateWithLifecycle()

    return remember {
        MainComposeState(
            title = title,
            message = message,
            onTitleChanged = { insert -> presenter.onTitleChanged(insert) },
            onMessageChanged = { insert -> presenter.onMessageChanged(insert) },
            onPost = presenter::onPost,
            onClose = presenter::onClose
        )
    }
}