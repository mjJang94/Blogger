package com.mj.blogger.ui.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.login.presentation.LoginScreen
import com.mj.blogger.ui.login.presentation.LoginState
import com.mj.blogger.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.mj.blogger.ui.login.presentation.SignType as Type

@AndroidEntryPoint
class LoginActivity: AppCompatActivity() {

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

        setContent {
            BloggerTheme {
                LoginScreen()
            }
        }
    }

    @Composable
    private fun LoginScreen() {
        viewModel.signEvent.observe { info ->
            when (info.requestType) {
                Type.SIGN_IN -> signIn(info.id, info.password)
                Type.SIGN_UP -> signUp(info.id, info.password)
            }
        }

        viewModel.loginEvent.observe { result ->
            when (result){
                LoginState.SUCCESS -> MainActivity.start(this)
                LoginState.FAIL -> Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }

        LoginScreen(presenter = viewModel)
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(this::class.simpleName, "createUserWithEmail:success")
                        val userId = auth.currentUser?.uid
                        viewModel.saveUserInfo(userId, email)
                    }
                    else -> {
                        Log.w(this::class.simpleName, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "계정생성에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(this::class.simpleName, "signInWithEmail:success")
                        val userId = auth.currentUser?.uid
                        viewModel.saveUserInfo(userId, email)
                    }
                    else -> {
                        Log.w(this::class.simpleName, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
