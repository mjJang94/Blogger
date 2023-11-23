@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.blogger.ui.main.presentation

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.CircularProgress
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.foundation.ImageCountDim
import com.mj.blogger.common.compose.ktx.ConvertMillisToFormattedDate
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import com.mj.blogger.ui.main.presentation.state.PostingItem

@Composable
fun MainHomeContent(
    state: MainContentState,
    recentScrollState: ScrollState = rememberScrollState(),
) {

    LaunchedEffect(state.recentPostingItems) {
        recentScrollState.animateScrollTo(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {

        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            WelcomeLabel()

            LoginLabel(email = state.email)

            RecentPostingCard(
                scrollState = recentScrollState,
                postingLoaded = state.postingLoaded,
                recentItems = state.recentPostingItems,
                onClick = state.openDetail,
            )

            HitsPostingCard(
                postingLoaded = state.postingLoaded,
                hitsItems = state.hitsPostingItems,
                onClick = state.openDetail,
            )
        }
    }
}

@Composable
private fun WelcomeLabel() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 10.dp),
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
    scrollState: ScrollState,
    postingLoaded: Boolean,
    recentItems: List<PostingItem>,
    onClick: (PostingItem) -> Unit,
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
        if (!postingLoaded) {
            RecentPlaceholder()
        } else {
            if (recentItems.isEmpty()) {
                EmptyRecentPostingList()
            } else {
                RecentPostingList(
                    scrollState = scrollState,
                    items = recentItems,
                    onClick = onClick,
                )
            }
        }
    }
}

@Composable
private fun EmptyRecentPostingList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.main_empty_recent_posting),
                fontSize = 12.sp,
                color = Color.Black,
            )
        }
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

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        repeat(3) {
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
    scrollState: ScrollState,
    items: List<PostingItem>,
    onClick: (PostingItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 1.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            Card(
                modifier = Modifier
                    .width(200.dp)
                    .height(250.dp),
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .clickable { onClick(item) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(0.7f)
                            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    ) {
                        if (item.thumbnail == null) {
                            Image(
                                modifier = Modifier.fillMaxSize(),
                                painter = painterResource(id = R.drawable.ic_baseline_article),
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

@Composable
private fun HitsPostingCard(
    postingLoaded: Boolean,
    hitsItems: List<PostingItem>,
    onClick: (PostingItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {

        Text(
            text = stringResource(R.string.main_hits_label),
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            elevation = 4.dp,
            shape = RoundedCornerShape(16.dp),
        ) {
            if (!postingLoaded) {
                return@Card HitsPlaceholder()
            } else {
                if (hitsItems.isEmpty()) {
                    EmptyHits()
                } else {
                    HitsPosting(
                        items = hitsItems,
                        onClick = onClick,
                    )
                }
            }
        }
    }
}

@Composable
private fun HitsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgress(loading = true)
    }
}

@Composable
private fun EmptyHits() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.main_empty_recent_posting),
            fontSize = 12.sp,
            color = Color.Black,
        )
    }
}

@Composable
private fun HitsPosting(
    items: List<PostingItem>,
    onClick: (PostingItem) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        items.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable { onClick(item) }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = (index + 1).toString(),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (index) {
                        0 -> Color(0XFFFFD700)
                        1 -> Color(0XFFC0C0C0)
                        else -> Color(0XFF88540B)
                    }
                )

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
                        text = stringResource(R.string.main_hits, item.hits),
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


@Composable
@Preview
private fun MainHomeContentPreview() {
    BloggerTheme {
        MainHomeContent(
            state = rememberPreviewMainContentState(),
            recentScrollState = rememberScrollState(),
        )
    }
}