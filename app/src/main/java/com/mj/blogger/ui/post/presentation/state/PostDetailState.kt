package com.mj.blogger.ui.post.presentation.state

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.post.presentation.PostDetailPresenter

@Stable
class PostDetailState(

    progressing: State<Boolean>,
    postImages: State<List<Uri?>>,
    postDetail: State<PostDetail?>,

    val back: () -> Unit,
    val modify: () -> Unit,
    val delete: () -> Unit,
) {
    val progressing by progressing
    val postImages by postImages
    val postDetail by postDetail
}

@Composable
fun rememberPostDetailState(
    presenter: PostDetailPresenter,
): PostDetailState {

    val progressing = presenter.progressing.collectAsStateWithLifecycle()
    val postDetail = presenter.postItem.collectAsStateWithLifecycle()
    val postImages = presenter.postImages.collectAsStateWithLifecycle()

    return remember {
        PostDetailState(
            progressing = progressing,
            postImages = postImages,
            postDetail = postDetail,
            back = presenter::onBack,
            modify = presenter::onModify,
            delete = presenter::onDelete,
        )
    }
}
