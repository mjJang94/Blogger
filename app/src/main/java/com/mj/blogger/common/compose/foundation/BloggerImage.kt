package com.mj.blogger.common.compose.foundation

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun BloggerImage(
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