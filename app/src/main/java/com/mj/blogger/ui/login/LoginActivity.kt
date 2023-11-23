package com.mj.blogger.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.collect
import com.mj.blogger.common.ktx.toast
import com.mj.blogger.ui.login.presentation.LoginScreen
import com.mj.blogger.ui.login.presentation.LoginState
import com.mj.blogger.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject
import com.mj.blogger.ui.login.presentation.SignType as Type

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    private val viewModel: LoginViewModel by viewModels()

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (auth.currentUser != null) {
            MainActivity.start(this@LoginActivity)
        }

        viewModel.signEvent.collect(this) { info ->
            when (info.requestType) {
                Type.SIGN_IN -> signIn(info.id, info.password)
                Type.SIGN_UP -> signUp(info.id, info.password)
            }
        }

        viewModel.loginEvent.collect(this) { result ->
            when (result) {
                LoginState.SUCCESS -> MainActivity.start(this)
                LoginState.FAIL ->  toast(getString(R.string.login_failure))
            }
        }

        setContent {
            BloggerTheme {
                LoginScreen(presenter = viewModel)
            }
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Timber.d("createUserWithEmail:success")
                        val userId = auth.currentUser?.uid
                        viewModel.saveUserInfo(userId, email)
                    }

                    else -> {
                        Timber.w("createUserWithEmail:failure = ${task.exception}")
                        toast(getString(R.string.login_signup_fail))
                    }
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Timber.d("signInWithEmail:success")
                        val userId = auth.currentUser?.uid
                        viewModel.saveUserInfo(userId, email)
                    }

                    else -> {
                        Timber.w("signInWithEmail:failure = ${task.exception}")
                        toast(getString(R.string.login_failure))
                    }
                }
            }
    }
}
