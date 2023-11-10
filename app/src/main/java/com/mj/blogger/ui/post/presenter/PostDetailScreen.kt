package com.mj.blogger.ui.post.presenter

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.ui.post.presenter.state.PostDetailState
import com.mj.blogger.ui.post.presenter.state.rememberPostDetailState

@Composable
fun PostDetailScreen(presenter: PostDetailPresenter) {
    PostDetailContent(rememberPostDetailState(presenter = presenter))
}

@Composable
private fun PostDetailContent(state: PostDetailState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        DetailToolbar(
            onClick = state.back
        )


    }
}

@Composable
private fun DetailToolbar(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Image(
            modifier = Modifier.clickable(onClick = onClick),
            painter = painterResource(id = R.drawable.ic_baseline_chevron_left)
        )
    }
}

@Preview
@Composable
private fun PostDetailContentPreview() {
    val state = PostDetailState(
        postDetail = remember{ mutableStateOf(null) },
        back = {}
    )
    PostDetailContent(state)
}