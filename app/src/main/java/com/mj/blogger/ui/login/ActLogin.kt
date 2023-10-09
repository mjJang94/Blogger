package com.mj.blogger.ui.login

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.login.presentation.LoginScreen
import com.mj.blogger.ui.login.presentation.SignType as Type
import com.mj.blogger.ui.main.ActMain

class ActLogin : AppCompatActivity() {

    private val viewModel: VMLogin by viewModels()

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        if (auth.currentUser != null) {
            ActMain.start(this@ActLogin)
        }

        setContent {
            BloggerTheme {
                LoginScreen()
            }
        }
    }

    @Composable
    private fun LoginScreen() {
        viewModel.signEvent.observe(this) { info ->
            when (info.requestType) {
                Type.SIGN_IN -> signIn(info.id, info.password)
                Type.SIGN_UP -> signUp(info.id, info.password)
            }
        }

        viewModel.enabled.observe(this) {
            Log.d(this::class.simpleName, "$it")
        }

        LoginScreen(presenter = viewModel)
    }

    private fun signUp(id: String, password: String) {
        auth.createUserWithEmailAndPassword(id, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(this::class.simpleName, "createUserWithEmail:success")
                        //move to main
                    }
                    else -> {
                        Log.w(this::class.simpleName, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this, "계정생성에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun signIn(id: String, password: String) {
        auth.signInWithEmailAndPassword(id, password)
            .addOnCompleteListener(this) { task ->
                when {
                    task.isSuccessful -> {
                        Log.d(this::class.simpleName, "signInWithEmail:success")
                        //move to main
                    }
                    else -> {
                        Log.w(this::class.simpleName, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this, "로그인에 실패하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}
