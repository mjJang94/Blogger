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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mj.blogger.R
import com.mj.blogger.common.compose.foundation.Image
import com.mj.blogger.common.compose.foundation.PageButton
import com.mj.blogger.common.compose.foundation.TextField
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.login.presentation.state.LoginState
import com.mj.blogger.ui.login.presentation.state.rememberLoginState
import com.mj.blogger.ui.login.presentation.SignType as Type

@Composable
fun LoginScreen(presenter: LoginPresenter) {
    LoginScreenContent(rememberLoginState(presenter = presenter))
}

@Composable
private fun LoginScreenContent(state: LoginState) {
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

        Image(
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
    PageButton(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        text = stringResource(R.string.login_sign_in),
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
    PageButton(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 30.dp),
        text = stringResource(R.string.login_sign_up),
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = email,
                onInsertChanged = onEmailChanged,
                hint = stringResource(R.string.login_email_hint),
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
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                insert = password,
                onInsertChanged = onPasswordChanged,
                hint = stringResource(R.string.login_password_hint),
                hintColor = Color.Gray,
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    val state = LoginState(
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