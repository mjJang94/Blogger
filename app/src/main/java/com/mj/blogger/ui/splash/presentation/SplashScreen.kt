package com.mj.blogger.ui.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.theme.BloggerTheme

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        Image(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_baseline_article),
        )

        ProgressContent(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ProgressContent(modifier: Modifier) {
    Column(
        modifier = modifier
            .padding(bottom = 15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        CircularProgressIndicator(
            modifier = Modifier.wrapContentSize(),
            color = colorResource(id = R.color.purple_200),
            strokeWidth = 2.dp
        )

        Text(
            text = stringResource(R.string.login_check_user_info),
            color = Color.Gray,
            fontSize = 10.sp,
        )
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    BloggerTheme {
        SplashScreen()
    }
}