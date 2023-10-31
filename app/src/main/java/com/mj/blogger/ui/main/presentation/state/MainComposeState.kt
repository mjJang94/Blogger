package com.mj.blogger.ui.main.presentation.state

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.MainComposePresenter

@Stable
class MainComposeState(
    title: State<String>,
    message: State<String>,
    images: State<List<Uri>>,
    imagePosition: State<Pair<Int,Uri>?>,

    val onTitleChanged: (String) -> Unit,
    val onMessageChanged: (String) -> Unit,
    val onPickImage: () -> Unit,
    val onPost: () -> Unit,
    val onClose: () -> Unit,
) {
    val title by title
    val message by message
    val images by images
    val imagePosition by imagePosition
}

@Composable
fun rememberMainComposeState(
    presenter: MainComposePresenter,
): MainComposeState {

    val title = presenter.title.collectAsStateWithLifecycle()
    val message = presenter.message.collectAsStateWithLifecycle()
    val images = presenter.images.collectAsStateWithLifecycle()
    val imagePosition = presenter.imageWithPosition.collectAsStateWithLifecycle()

    return remember {
        MainComposeState(
            title = title,
            message = message,
            images = images,
            imagePosition = imagePosition,
            onTitleChanged = { insert -> presenter.onTitleChanged(insert) },
            onMessageChanged = { insert -> presenter.onMessageChanged(insert) },
            onPickImage = presenter::onPickImage,
            onPost = presenter::onPost,
            onClose = presenter::onClose
        )
    }
}