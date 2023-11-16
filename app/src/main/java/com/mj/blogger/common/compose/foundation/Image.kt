@file:OptIn(ExperimentalGlideComposeApi::class, ExperimentalGlideComposeApi::class)

package com.mj.blogger.common.compose.foundation

import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.integration.compose.Placeholder
import com.bumptech.glide.integration.compose.RequestBuilderTransform

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
    uri: Uri?,
    contentDescription: String? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    loading: Placeholder? = null,
    failure: Placeholder? = null,
    requestBuilderTransform: RequestBuilderTransform<Drawable> = { it },
) {
    GlideImage(
        modifier = modifier,
        model = uri,
        contentDescription = contentDescription,
        contentScale = contentScale,
        alignment = alignment,
        alpha = alpha,
        colorFilter = colorFilter,
        loading = loading,
        failure = failure,
        requestBuilderTransform = requestBuilderTransform,
    )
}