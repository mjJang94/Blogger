package com.mj.blogger.ui.main.presentation

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.foundation.TextField
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainComposeState
import com.mj.blogger.ui.main.presentation.state.rememberMainComposeState

@Composable
fun MainComposeScreen(presenter: MainComposePresenter) {
    MainComposeContent(
        rememberMainComposeState(presenter = presenter)
    )
}

@Composable
fun MainComposeContent(state: MainComposeState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ComposeToolbar(
            close = state.onClose,
            action = state.onPost,
            pickImage = state.onPickImage,
            images = state.images,
        )
        TitleField(
            title = state.title,
            titleChanged = state.onTitleChanged,
        )
        ContentField(
            images = state.images,
            message = state.message,
            messageChanged = state.onMessageChanged,
        )
    }
}

@Composable
private fun ComposeToolbar(
    close: () -> Unit,
    action: () -> Unit,
    pickImage: () -> Unit,
    images: List<Uri>,
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

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = when (images.isEmpty()) {
                                    true -> Color.LightGray
                                    else -> Color.Red
                                }
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = pickImage)
                        .then(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ),
                    text = when (images.isEmpty()) {
                        true -> stringResource(R.string.compose_image)
                        else -> stringResource(R.string.compose_images, images.size)
                    },
                    color = when (images.isEmpty()) {
                        true -> Color.Black
                        else -> Color.Red
                    },
                    fontSize = 12.sp,
                )

                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(end = 12.dp)
                        .border(
                            border = BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clip(RoundedCornerShape(16.dp))
                        .clickable(onClick = action)
                        .then(
                            Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ),
                    text = stringResource(R.string.compose_complete),
                    color = Color.Black,
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
        textSize = 20.sp,
    )
}

@Composable
private fun ContentField(
    images: List<Uri>,
    message: String,
    messageChanged: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 20.dp)
    ) {
        if (images.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.wrapContentSize(),
                state = rememberLazyListState(),
            ) {
                items(
                    items = images,
                    key = { it },
                ) { uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Image(
                            modifier = Modifier.size(150.dp),
                            uri = uri,
                        )

                        Image(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.TopEnd)
                                .padding(top = 10.dp, end = 10.dp),
                            painter = painterResource(id = R.drawable.ic_baseline_cancel)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {

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
}

@Composable
@Preview
private fun MainComposeScreenPreview() {
    val state = MainComposeState(
        title = remember { mutableStateOf("") },
        message = remember { mutableStateOf("") },
        images = remember { mutableStateOf(emptyList()) },
        imagePosition = remember { mutableStateOf(0 to Uri.parse("")) },
        onTitleChanged = {},
        onMessageChanged = {},
        onPickImage = {},
        onPost = {},
        onClose = {},
    )

    BloggerTheme {
        MainComposeContent(state)
    }
}