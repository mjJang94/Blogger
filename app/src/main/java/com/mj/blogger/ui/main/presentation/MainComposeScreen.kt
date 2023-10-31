package com.mj.blogger.ui.main.presentation

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            action = state.onPost
        )
        TitleField(
            title = state.title,
            titleChanged = state.onTitleChanged,
        )
        ContentField(
            message = state.message,
            messageChanged = state.onMessageChanged,
            onPickImage = state.onPickImage,
        )
    }
}

@Composable
private fun ComposeToolbar(
    close: () -> Unit,
    action: () -> Unit,
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

            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(end = 12.dp)
                    .border(
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(onClick = action),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
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
    message: String,
    messageChanged: (String) -> Unit,
    onPickImage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(horizontal = 20.dp)
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            insert = message,
            hint = stringResource(R.string.compose_message),
            onInsertChanged = messageChanged,
            textColor = Color.Black,
            hintColor = Color.Gray,
            textSize = 16.sp,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Image(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable(onClick = onPickImage),
                painter = painterResource(id = R.drawable.ic_outline_image)
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
        images = remember { mutableStateOf(listOf(Uri.parse(""))) },
        imagePosition = remember{ mutableStateOf(0 to Uri.parse("")) },
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