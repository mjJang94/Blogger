@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.blogger.ui.main.presentation

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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.CircularProgress
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.foundation.ImageCountDim
import com.mj.blogger.common.compose.ktx.ConvertMillisToFormattedDate
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.PostingItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainBlogContent(
    state: MainContentState,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        if (state.postingLoaded) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                AllPostingCard(
                    items = rememberImmutableList(state.allPostingItems),
                    onOpenDetail = state.openDetail,
                )
            }
        } else {
            CircularProgress(true)
        }
    }
}

@Composable
private fun AllPostingCard(
    items: ImmutableList<PostingItem>,
    listState: LazyListState = rememberLazyListState(),
    onOpenDetail: (PostingItem) -> Unit,
) {

    LaunchedEffect(items) {
        listState.animateScrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            text = stringResource(R.string.main_category_label),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )

        LazyColumn(
            modifier = Modifier.wrapContentSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(
                items = items,
                key = { it.postTime },
            ) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { onOpenDetail(item) }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text(
                            text = item.title,
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            text = ConvertMillisToFormattedDate(millis = item.postTime),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }

                    Box(
                        modifier = Modifier.size(50.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (item.thumbnail == null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painterResource(id = R.drawable.ic_baseline_article),
                            )
                        } else {
                            GlideImage(
                                modifier = Modifier.fillMaxSize(),
                                model = item.thumbnail,
                                contentScale = ContentScale.Crop,
                            )
                            ImageCountDim(item.images.size)
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun MainBlogContentPreview() {
    BloggerTheme {
        MainBlogContent(
            state = rememberPreviewMainContentState(),
        )
    }
}