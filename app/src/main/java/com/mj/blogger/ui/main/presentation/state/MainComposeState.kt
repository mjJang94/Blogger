package com.mj.blogger.ui.main.presentation.state

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.MainComposePresenter

@Stable
class MainComposeState(
    isModify: State<Boolean>,
    title: State<String>,
    message: State<String>,
    images: State<List<Uri>>,
    imagesCount: State<Int>,

    val onTitleChanged: (String) -> Unit,
    val onMessageChanged: (String) -> Unit,
    val onImageCancel: (Int) -> Unit,
    val onPickImage: () -> Unit,
    val onPost: () -> Unit,
    val onModify: () -> Unit,
    val onClose: () -> Unit,
) {
    val isModify by isModify
    val title by title
    val message by message
    val images by images
    val imagesCount by imagesCount
}

@Composable
fun rememberMainComposeState(
    presenter: MainComposePresenter,
): MainComposeState {

    val isModify = presenter.isModify.collectAsStateWithLifecycle()
    val title = presenter.title.collectAsStateWithLifecycle()
    val message = presenter.message.collectAsStateWithLifecycle()
    val images = presenter.images.collectAsStateWithLifecycle()
    val imagesCount = presenter.imagesCount.collectAsStateWithLifecycle()

    return remember {
        MainComposeState(
            isModify = isModify,
            title = title,
            message = message,
            images = images,
            imagesCount = imagesCount,
            onTitleChanged = { insert -> presenter.onTitleChanged(insert) },
            onMessageChanged = { insert -> presenter.onMessageChanged(insert) },
            onPickImage = presenter::onPickImage,
            onImageCancel = presenter::onImageCancel,
            onPost = presenter::onPost,
            onModify = presenter::onModify,
            onClose = presenter::onClose
        )
    }
}