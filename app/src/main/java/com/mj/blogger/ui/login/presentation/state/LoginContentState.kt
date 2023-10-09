package com.mj.blogger.ui.login.presentation.state

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.blogger.ui.login.presentation.LoginPresenter
import com.mj.blogger.ui.login.presentation.SignType

@Stable
class LoginContentState(
    email: State<String>,
    password: State<String>,
    enabled: State<Boolean>,

    val onEmailChanged: (String) -> Unit,
    val onPasswordChanged: (String) -> Unit,
    val onSign: (type: SignType) -> Unit,
) {
    val email by email
    val password by password
    val enabled by enabled
}

@Composable
fun rememberLoginContentState(
    presenter: LoginPresenter,
): LoginContentState {

    val email = presenter.email.collectAsStateWithLifecycle()
    val password = presenter.password.collectAsStateWithLifecycle()
    val enabled = presenter.enabled.collectAsStateWithLifecycle()

    return remember {
        LoginContentState(
            email = email,
            password = password,
            enabled = enabled,
            onEmailChanged = { insert -> presenter.onEmailChanged(insert) },
            onPasswordChanged = { insert -> presenter.onPasswordChanged(insert) },
            onSign = presenter::onSign,
        )
    }
}