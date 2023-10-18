package com.mj.blogger.ui.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.theme.BloggerTheme

@Composable
fun SplashScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.ic_baseline_article),
        )

        Spacer(modifier = Modifier.height(10.dp))

        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun SplashScreenPreview() {
    BloggerTheme {
        SplashScreen()
    }
}