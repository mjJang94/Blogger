package com.mj.blogger.ui.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.mj.blogger.ui.login.LoginActivity
import com.mj.blogger.ui.main.MainActivity
import com.mj.blogger.ui.splash.presentation.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }

        viewModel.waitForLoading { result ->
            when (result) {
                true -> MainActivity.start(this@SplashActivity)
                else -> LoginActivity.start(this@SplashActivity)
            }
        }
    }
}