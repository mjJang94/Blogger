package com.mj.blogger.ui.post.presenter.state

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.main.presentation.state.PostingItem
import com.mj.blogger.ui.post.presenter.PostDetailPresenter

@Stable
class PostDetailState(

    postDetail: State<PostDetail?>,

    val back: () -> Unit,
) {
    val postDetail by postDetail
}

@Composable
fun rememberPostDetailState(
    presenter: PostDetailPresenter,
): PostDetailState {

    val postDetail = presenter.postItem.collectAsStateWithLifecycle()

    return remember {
        PostDetailState(
            postDetail = postDetail,
            back = presenter::onBack
        )
    }
}
