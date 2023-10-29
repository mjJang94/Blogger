package com.mj.blogger.ui.post.presenter

import androidx.compose.runtime.Composable
import com.mj.blogger.ui.post.presenter.state.PostDetailState
import com.mj.blogger.ui.post.presenter.state.rememberPostDetailState

@Composable
fun PostDetailScreen(presenter: PostDetailPresenter) {
    PostDetailContent(rememberPostDetailState(presenter = presenter))
}

@Composable
private fun PostDetailContent(state: PostDetailState) {

}