@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.post.presenter

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.ui.post.presenter.state.PostDetail
import com.mj.blogger.ui.post.presenter.state.PostDetailState
import com.mj.blogger.ui.post.presenter.state.rememberPostDetailState
import kotlinx.collections.immutable.ImmutableList

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

        ImageContent(
            items = rememberImmutableList(list = state.postImages)
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
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .clickable(onClick = onClick),
            painter = painterResource(id = R.drawable.ic_baseline_chevron_left)
        )
    }
}

@Composable
private fun ImageContent(
    items: ImmutableList<Uri?>,
) {
    if (items.isNotEmpty()) {

        val pagerState = rememberPagerState { items.size }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            contentAlignment = Alignment.BottomCenter,
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    uri = items[page],
                )
            }

            PagerIndicator(
                modifier = Modifier
                    .padding(8.dp),
                pageCount = items.size,
                currentPage = pagerState.currentPage
            )
        }
    }
}

@Composable
fun PagerIndicator(
    modifier: Modifier = Modifier,
    pageCount: Int,
    currentPage: Int,
    indicatorSize: Dp = 8.dp,
    indicatorSpacing: Dp = 8.dp,
    indicatorColor: Color = Color.Gray,
    selectedIndicatorColor: Color = colorResource(id = R.color.purple_200),
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(indicatorSpacing)
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(indicatorSize)
                    .background(
                        color = if (index == currentPage) selectedIndicatorColor else indicatorColor,
                        shape = CircleShape
                    )
            )
        }
    }
}

@Preview
@Composable
private fun PostDetailContentPreview() {
    val state = PostDetailState(
        postImages = remember { mutableStateOf(emptyList()) },
        postDetail = remember { mutableStateOf(null) },
        back = {}
    )
    PostDetailContent(state)
}