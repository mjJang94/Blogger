package com.mj.blogger.ui.post.presenter.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import com.mj.blogger.ui.post.presenter.PostDetailPresenter

@Stable
class PostDetailState(

){

}

@Composable
fun rememberPostDetailState(
    presenter: PostDetailPresenter
): PostDetailState {

    return remember {
        PostDetailState(

        )
    }
}
