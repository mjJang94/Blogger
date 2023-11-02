@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
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
            items = rememberImmutableList(state.recentPostingItems),
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
    items: ImmutableList<PostingItem>,
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

        LazyRow(
            modifier = Modifier.wrapContentSize(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 5.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                            .padding(10.dp),
                    ) {
                        Image(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.7f),
                            painter = painterResource(id = R.drawable.ic_baseline_article)
                        )
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.2f),
                            text = item.title,
                            fontSize = 14.sp,
                            color = Color.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.1f),
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
}

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