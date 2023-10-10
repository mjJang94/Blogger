package com.mj.blogger.ui.login.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.BloggerImage
import com.mj.blogger.common.compose.foundation.BloggerMaskingTextField
import com.mj.blogger.common.compose.foundation.BloggerPageButton
import com.mj.blogger.common.compose.foundation.BloggerTextField
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.login.presentation.state.LoginContentState
import com.mj.blogger.ui.login.presentation.state.rememberLoginContentState
import com.mj.blogger.ui.login.presentation.SignType as Type

@Composable
fun LoginScreen(presenter: LoginPresenter) {
    LoginScreenContent(rememberLoginContentState(presenter = presenter))
}

@Composable
private fun LoginScreenContent(state: LoginContentState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        val available by remember {
            derivedStateOf { state.enabled }
        }

        BloggerImage(
            modifier = Modifier.size(100.dp),
            painter = painterResource(id = R.drawable.ic_baseline_article),
        )

        Spacer(modifier = Modifier.height(10.dp))

        LoginTextField(
            email = state.email,
            password = state.password,
            onEmailChanged = state.onEmailChanged,
            onPasswordChanged = state.onPasswordChanged,
        )

        Spacer(modifier = Modifier.height(30.dp))

        SignInButton(
            enabled = available,
            onClick = { state.onSign(Type.SIGN_IN) },
        )

        Spacer(modifier = Modifier.height(10.dp))

        SignUpButton(
            enabled = available,
            onClick = { state.onSign(Type.SIGN_UP) },
        )
    }
}

@Composable
private fun SignInButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    BloggerPageButton(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        text = "로그인",
        textColor = Color.White,
        textSize = 16.sp,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun SignUpButton(
    enabled: Boolean,
    onClick: () -> Unit,
) {
    BloggerPageButton(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        text = "회원가입",
        textColor = Color.White,
        textSize = 16.sp,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
private fun LoginTextField(
    email: String,
    password: String,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
    ) {

        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            BloggerTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = email,
                onInsertChanged = onEmailChanged,
                hint = "이메일 주소",
                hintColor = Color.Gray,
            )
        }


        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .wrapContentSize()
                .border(width = 1.dp, color = Color.LightGray, shape = RoundedCornerShape(16.dp))
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            BloggerMaskingTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = password,
                onInsertChanged = onPasswordChanged,
                hint = "비밀번호",
                hintColor = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val state = LoginContentState(
        email = remember { mutableStateOf("") },
        password = remember { mutableStateOf("") },
        enabled = remember { mutableStateOf(false) },
        onEmailChanged = {},
        onPasswordChanged = {},
        onSign = {}
    )
    BloggerTheme {
        LoginScreenContent(state)
    }
}