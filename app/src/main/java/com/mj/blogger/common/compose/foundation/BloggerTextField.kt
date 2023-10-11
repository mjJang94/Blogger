package com.mj.blogger.common.compose.foundation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme

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
    Box(modifier = modifier) {

        BasicTextField(
            value = insert,
            onValueChange = onInsertChanged,
            textStyle = TextStyle(
                fontFamily = LocalTextStyle.current.fontFamily,
                fontSize = textSize,
                color = textColor
            ),
            cursorBrush = SolidColor(cursorColor),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    if (insert.isEmpty()) {
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
fun BloggerMaskingTextField(
    modifier: Modifier,
    insert: String,
    hint: String = "",
    onInsertChanged: (String) -> Unit,
    textSize: TextUnit = 16.sp,
    textColor: Color = LocalTextStyle.current.color,
    hintColor: Color = LocalTextStyle.current.color,
    cursorColor: Color = colorResource(id = R.color.purple_200),
) {

    var isMasked by remember { mutableStateOf(true) }

    Box(modifier = modifier) {

        BasicTextField(
            value = insert,
            onValueChange = onInsertChanged,
            textStyle = TextStyle(
                fontFamily = LocalTextStyle.current.fontFamily,
                fontSize = textSize,
                color = textColor
            ),
            cursorBrush = SolidColor(cursorColor),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (isMasked) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (insert.isEmpty()) {
                        InsertHint(
                            hint = hint,
                            hintColor = hintColor,
                            hintSize = textSize,
                        )
                    }

                    innerTextField()

                    BloggerImage(
                        modifier = Modifier.clickable(
                            onClick = { isMasked = !isMasked }
                        ),
                        painter = painterResource(
                            id = when (isMasked) {
                                true -> R.drawable.ic_baseline_visibility_off
                                else -> R.drawable.ic_baseline_visibility_on
                            }
                        )
                    )
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

@Preview
@Composable
private fun BloggerTextFieldPreview() {
    BloggerTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            BloggerTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = "123",
                onInsertChanged = {},
            )
            BloggerMaskingTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = "123",
                onInsertChanged = {},
            )
        }
    }
}