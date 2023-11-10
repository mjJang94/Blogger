@file:OptIn(ExperimentalFoundationApi::class)

package com.mj.blogger.ui.main.presentation

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material.Card
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.GlideImage
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.foundation.TextField
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainComposeState
import com.mj.blogger.ui.main.presentation.state.rememberMainComposeState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainComposeScreen(presenter: MainComposePresenter) {
    MainComposeContent(
        rememberMainComposeState(presenter = presenter)
    )
}

@Composable
fun MainComposeContent(state: MainComposeState) {

    val allowCompose by remember {
        derivedStateOf {
            state.title.isNotBlank() && (state.message.isNotBlank() || state.images.isNotEmpty())
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ComposeToolbar(
            allowCompose = allowCompose,
            imageCount = state.imagesCount,
            close = state.onClose,
            post = state.onPost,
            pickImage = state.onPickImage,
        )
        TitleField(
            title = state.title,
            titleChanged = state.onTitleChanged,
        )
        ContentField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            images = rememberImmutableList(state.images),
            message = state.message,
            messageChanged = state.onMessageChanged,
            imageCancel = state.onImageCancel,
        )
    }
}

@Composable
private fun ComposeToolbar(
    allowCompose: Boolean,
    imageCount: Int,
    close: () -> Unit,
    post: () -> Unit,
    pickImage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(onClick = close),
                contentAlignment = Alignment.Center,
            ) {
                Image(painter = painterResource(id = R.drawable.ic_baseline_close))
            }

            Row(
                modifier = Modifier.wrapContentSize(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                val imagePickButtonColor = when (imageCount > 0) {
                    true -> Color.Red
                    else -> Color.Black
                }

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = imagePickButtonColor,
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = pickImage)
                        .then(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ),
                    text = when (imageCount > 0) {
                        true -> stringResource(R.string.compose_images, imageCount)
                        else -> stringResource(R.string.compose_image)
                    },
                    color = imagePickButtonColor,
                    fontSize = 12.sp,
                )

                val composeButtonColor = when (allowCompose) {
                    true -> Color.Black
                    else -> Color.LightGray

                }
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 12.dp)
                        .border(
                            border = BorderStroke(1.dp, composeButtonColor),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(
                            enabled = allowCompose,
                            onClick = post
                        )
                        .then(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ),
                    text = stringResource(R.string.compose_complete),
                    color = composeButtonColor,
                    fontSize = 12.sp,
                )
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.LightGray,
        )
    }
}

@Composable
private fun TitleField(
    title: String,
    titleChanged: (String) -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp),
        insert = title,
        hint = stringResource(R.string.compose_title),
        onInsertChanged = titleChanged,
        textColor = Color.Black,
        hintColor = Color.Gray,
        textSize = 24.sp,
    )
}

@Composable
private fun ContentField(
    modifier: Modifier,
    images: ImmutableList<Uri>,
    message: String,
    messageChanged: (String) -> Unit,
    imageCancel: (Int) -> Unit,
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.wrapContentSize(),
                state = rememberLazyListState(),
                contentPadding = PaddingValues(horizontal = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                itemsIndexed(
                    items = images,
                    key = { _, uri -> uri },
                ) { index, uri ->
                    Card(
                        modifier = Modifier
                            .size(150.dp)
                            .animateItemPlacement(),
                        elevation = 2.dp,
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Box {
                            GlideImage(
                                modifier = Modifier.fillMaxSize(),
                                uri = uri,
                                scale = ContentScale.Crop
                            )

                            Image(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.TopEnd)
                                    .clickable { imageCancel.invoke(index) }
                                    .then(
                                        Modifier.padding(top = 10.dp, end = 10.dp)
                                    ),
                                painter = painterResource(id = R.drawable.ic_baseline_cancel)
                            )
                        }
                    }
                }
            }
        }
        TextField(
            modifier = Modifier.wrapContentSize(),
            insert = message,
            hint = stringResource(R.string.compose_message),
            onInsertChanged = messageChanged,
            textColor = Color.Black,
            hintColor = Color.Gray,
            textSize = 16.sp,
        )
    }
}

@Composable
@Preview
private fun MainComposeScreenPreview() {
    val state = MainComposeState(
        title = remember { mutableStateOf("") },
        message = remember { mutableStateOf("") },
        images = remember { mutableStateOf(emptyList()) },
        imagesCount = remember { mutableStateOf(0) },
        onTitleChanged = {},
        onMessageChanged = {},
        onImageCancel = {},
        onPickImage = {},
        onPost = {},
        onClose = {},
    )

    BloggerTheme {
        MainComposeContent(state)
    }
}