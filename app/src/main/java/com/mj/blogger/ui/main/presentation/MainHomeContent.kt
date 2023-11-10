@file:OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationGraphicsApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.ktx.ConvertMillisToFormattedDate
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.PostingItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainHomeContent(
    state: MainContentState,
    listState: LazyListState = rememberLazyListState(),
) {

    LaunchedEffect(state.recentPostingItems) {
        listState.animateScrollToItem(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        WelcomeLabel()

        LoginLabel(email = state.email)

        RecentPostingCard(
            listState = listState,
            recentItems = state.recentPostingItems,
            onClick = state.openDetail,
        )
    }
}

@Composable
private fun WelcomeLabel() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 58.dp),
        text = stringResource(R.string.main_greeting_label),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun LoginLabel(email: String) {
    Text(
        text = email,
        fontSize = 12.sp,
        color = Color.Black,
    )
}

@Composable
private fun RecentPostingCard(
    listState: LazyListState,
    recentItems: List<PostingItem>,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            text = stringResource(R.string.main_recent_label),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )

        if (recentItems.isEmpty()) {
            EmptyRecentList()
        } else {
            RecentPostingList(
                listState = listState,
                items = rememberImmutableList(list = recentItems),
                onClick = onClick,
            )
        }

//        if (allItems.isEmpty()) {
//            EmptyAllPosting()
//        } else {
//            AllPostingList(
//                items = rememberImmutableList(list = allItems)
//            )
//        }
    }
}

@Composable
private fun EmptyRecentList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 10.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Text(
            text = "작성한 글이 없습니다.",
            fontSize = 12.sp,
            color = Color.Black,
        )
    }
}

@Composable
private fun RecentPlaceholder() {
    val infiniteTransition = rememberInfiniteTransition(label = "infiniteTransition")
    val color by infiniteTransition.animateColor(
        initialValue = Color.Black,
        targetValue = Color.White,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "animateColor"
    )

    LazyRow(
        modifier = Modifier.wrapContentSize(),
        contentPadding = PaddingValues(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = 3) {
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .height(250.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color),
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        painter = painterResource(id = R.drawable.ic_baseline_article_placeholder)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentPostingList(
    listState: LazyListState,
    items: ImmutableList<PostingItem>,
    onClick: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier.wrapContentSize(),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 1.dp),
    ) {
        items(
            items = items,
            key = { it.postId },
        ) { item ->
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .height(250.dp)
                    .animateItemPlacement(),
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable { onClick(item.postId) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        if (item.image == null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = R.drawable.ic_baseline_article),
                            )
                        } else {
                            GlideImage(
                                modifier = Modifier.fillMaxSize(),
                                uri = item.image,
                                scale = ContentScale.Crop,
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.2f)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        text = item.title,
                        fontSize = 16.sp,
                        color = Color.Black,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.1f)
                            .padding(horizontal = 10.dp)
                            .then(
                                Modifier.padding(bottom = 5.dp)
                            ),
                        text = ConvertMillisToFormattedDate(millis = item.postTime),
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

//@Composable
//private fun EmptyAllPosting() {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 10.dp),
//    ) {
//        Text(
//            text = "작성한 글이 없습니다.",
//            fontSize = 12.sp,
//            color = Color.Black,
//        )
//    }
//}
//@Composable
//private fun AllPostingList(
//    items: ImmutableList<PostingItem>,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(top = 10.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//    ) {
//
//        Text(
//            text = stringResource(R.string.main_category_label),
//            fontSize = 14.sp,
//            color = Color.Black,
//            fontWeight = FontWeight.Bold,
//        )
//
//        LazyColumn(
//            modifier = Modifier.wrapContentSize(),
//            state = rememberLazyListState(),
//            verticalArrangement = Arrangement.spacedBy(8.dp),
//        ) {
//            items(
//                items = items,
//                key = { it.postTime },
//            ) { item ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(80.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Column(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .weight(1f),
//                        verticalArrangement = Arrangement.spacedBy(5.dp)
//                    ) {
//                        Text(
//                            text = item.title,
//                            fontSize = 14.sp,
//                            color = Color.Black,
//                            maxLines = 2,
//                            overflow = TextOverflow.Ellipsis,
//                        )
//
//                        Text(
//                            text = ConvertMillisToFormattedDate(millis = item.postTime),
//                            fontSize = 12.sp,
//                            color = Color.Gray,
//                            maxLines = 1,
//                            overflow = TextOverflow.Ellipsis,
//                        )
//                    }
//
//                    Image(
//                        modifier = Modifier.size(50.dp),
//                        painter = painterResource(id = R.drawable.ic_baseline_article)
//                    )
//                }
//            }
//        }
//    }
//}

@Composable
@Preview
private fun MainHomeContentPreview() {
    BloggerTheme {
        MainHomeContent(
            state = rememberPreviewMainContentState(),
            listState = rememberLazyListState(),
        )
    }
}