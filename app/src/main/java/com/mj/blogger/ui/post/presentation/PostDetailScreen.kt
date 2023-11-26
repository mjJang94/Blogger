@file:OptIn(ExperimentalFoundationApi::class, ExperimentalGlideComposeApi::class)

package com.mj.blogger.ui.post.presentation

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.CircularProgress
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.ktx.ConvertMillisToFormattedDate
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.ui.post.presentation.state.PostDetail
import com.mj.blogger.ui.post.presentation.state.PostDetailState
import com.mj.blogger.ui.post.presentation.state.rememberPostDetailState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun PostDetailScreen(presenter: PostDetailPresenter) {
    PostDetailContent(rememberPostDetailState(presenter = presenter))
}

@Composable
private fun PostDetailContent(
    state: PostDetailState,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState()),
        ) {
            DetailToolbar(onBack = state.back)

            PostContent(
                modifier = Modifier.weight(1f),
                postImages = state.postImages,
                postDetail = state.postDetail,
            )

            BottomNavigation(
                backgroundColor = Color.White,
                elevation = 12.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = state.modify),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_baseline_modify))
                    }

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable(onClick = state.delete),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(painter = painterResource(id = R.drawable.ic_outline_delete))
                    }
                }
            }
        }

        CircularProgress(showing = state.progressing)
    }
}

@Composable
private fun DetailToolbar(
    onBack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center,
        ) {
            Image(painter = painterResource(id = R.drawable.ic_baseline_chevron_left))
        }
    }
}

@Composable
private fun PostContent(
    modifier: Modifier,
    postImages: List<Uri?>,
    postDetail: PostDetail?,
) {
    if (postDetail != null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 10.dp),
                text = postDetail.title,
                color = Color.Black,
                fontSize = 24.sp,
            )

            ContentInformation(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp),
                hits = postDetail.hits,
                postTime = postDetail.postTime,
            )

            ImageContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(bottom = 20.dp),
                items = rememberImmutableList(list = postImages)
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 20.dp),
                text = postDetail.message,
                color = Color.Black,
                fontSize = 16.sp,
            )
        }
    }
}

@Composable
private fun ContentInformation(
    modifier: Modifier,
    hits: Int,
    postTime: Long,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {

        Row(
            modifier = Modifier.wrapContentSize(),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(painter = painterResource(id = R.drawable.ic_baseline_hits))

            Text(
                text = hits.toString(),
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.End,
            )
        }
        Text(
            text = ConvertMillisToFormattedDate(postTime),
            color = Color.Gray,
            fontSize = 14.sp,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun ImageContent(
    modifier: Modifier,
    items: ImmutableList<Uri?>,
) {
    if (items.isNotEmpty()) {

        val pagerState = rememberPagerState { items.size }

        Box(
            modifier = modifier,
            contentAlignment = Alignment.BottomCenter,
        ) {
            HorizontalPager(
                state = pagerState,
            ) { page ->
                GlideImage(
                    modifier = Modifier.fillMaxSize(),
                    model = items[page],
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
private fun PagerIndicator(
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

    val postDetail = PostDetail(
        postId = "",
        title = "안드로이드 Compose에 대해 알아봅시다.",
        message = "포스팅 내용.",
        postTime = 1L,
        hits = 0,
        images = emptyList(),
    )
    val state = PostDetailState(
        progressing = remember { mutableStateOf(true) },
        postImages = remember { mutableStateOf(emptyList()) },
        postDetail = remember { mutableStateOf(postDetail) },
        back = {},
        modify = {},
        delete = {},
    )
    PostDetailContent(state)
}