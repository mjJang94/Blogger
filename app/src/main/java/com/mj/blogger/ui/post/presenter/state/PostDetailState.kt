package com.mj.blogger.ui.post.presenter.state

import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.post.presenter.PostDetailPresenter

@Stable
class PostDetailState(

    postImages: State<List<Uri?>>,
    postDetail: State<PostDetail?>,

    val back: () -> Unit,
    val modify: () -> Unit,
    val delete: () -> Unit,
) {
    val postImages by postImages
    val postDetail by postDetail
}

@Composable
fun rememberPostDetailState(
    presenter: PostDetailPresenter,
): PostDetailState {

    val postDetail = presenter.postItem.collectAsStateWithLifecycle()
    val postImages = presenter.postImages.collectAsStateWithLifecycle()

    return remember {
        PostDetailState(
            postImages = postImages,
            postDetail = postDetail,
            back = presenter::onBack,
            modify = presenter::onModify,
            delete = presenter::onDelete,
        )
    }
}
