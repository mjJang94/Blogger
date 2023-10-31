package com.mj.blogger.ui.post.presenter

import com.mj.blogger.ui.post.presenter.state.PostDetail
import kotlinx.coroutines.flow.StateFlow

interface PostDetailPresenter {

    val post: StateFlow<PostDetail?>
}