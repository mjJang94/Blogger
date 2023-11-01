package com.mj.blogger.common.compose.foundation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

@Composable
fun Image(
    modifier: Modifier = Modifier,
    painter: Painter,
    contentDescription: String? = null,
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
    )
}

@Composable
fun Image(
    modifier: Modifier = Modifier,
    uri: Uri,
    scale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(model = uri),
        contentDescription = contentDescription,
        contentScale = scale,
    )
}