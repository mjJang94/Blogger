package com.mj.blogger.common.compose.foundation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.mj.blogger.R

@Composable
fun BloggerTextField(
    modifier: Modifier,
    insert: String,
    hint: String = "",
    onInsertChanged: (String) -> Unit,
    textSize: TextUnit = 16.sp,
    textColor: Color = LocalTextStyle.current.color,
    hintColor: Color = LocalTextStyle.current.color,
    cursorColor: Color = colorResource(id = R.color.purple_200),
) {
    Box(modifier = modifier){

        BasicTextField(
            value = insert,
            onValueChange = onInsertChanged,
            textStyle = TextStyle(
                fontFamily = LocalTextStyle.current.fontFamily,
                fontSize = textSize,
                color = textColor
            ),
            cursorBrush = Brush.verticalGradient(
                0.00f to cursorColor,
                1.00f to cursorColor,
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    if (insert.isEmpty()){
                        InsertHint(
                            hint = hint,
                            hintColor = hintColor,
                            hintSize = textSize,
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun InsertHint(
    hint: String,
    hintColor: Color,
    hintSize: TextUnit,
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        text = hint,
        color = hintColor,
        fontSize = hintSize,
    )
}