@file:OptIn(ExperimentalGlideComposeApi::class)

package com.mj.blogger.common.compose.foundation

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.placeholder

@Composable
fun Image(
    modifier: Modifier = Modifier,
    painter: Painter,
    scale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
) {
    Image(
        modifier = modifier,
        painter = painter,
        contentDescription = contentDescription,
        contentScale = scale,
    )
}

@Composable
fun GlideImage(
    modifier: Modifier = Modifier,
    uri: Uri,
    scale: ContentScale = ContentScale.Fit,
    contentDescription: String? = null,
) {
    GlideImage(
        modifier = modifier,
        model = uri,
        contentDescription = contentDescription,
        contentScale = scale,
    )
}