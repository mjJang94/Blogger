package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainCreateContent() {
    Column {
        Spacer(modifier = Modifier.fillMaxSize().background(Color.Blue))
    }
}

@Composable
@Preview
private fun MainCreateContentPreview() {

}