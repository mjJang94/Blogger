package com.mj.blogger.common.compose.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.mj.blogger.R

@Composable
fun PageButton(
    modifier: Modifier,
    text: String,
    textColor: Color,
    textSize: TextUnit,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(26.dp))
                .background(
                    when (enabled) {
                        true -> Color.LightGray
                        false -> colorResource(id = R.color.purple_200)
                    }
                )
                .clickable(
                    onClick = { if (!enabled) onClick.invoke() },
                )
                .padding(horizontal = 15.dp, vertical = 10.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = textSize,
            )
        }
    }
}