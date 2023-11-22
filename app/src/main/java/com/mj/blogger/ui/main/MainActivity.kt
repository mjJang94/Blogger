package com.mj.blogger.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.collect
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.compose.MainComposeActivity
import com.mj.blogger.ui.login.LoginActivity
import com.mj.blogger.ui.main.MainViewModel.InvalidUserException
import com.mj.blogger.ui.main.presentation.MainScreen
import com.mj.blogger.ui.post.PostDetailActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var auth: FirebaseAuth

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.composeEvent.collect(this) {
            MainComposeActivity.start(this)
        }

        viewModel.openDetailEvent.collect(this) { item ->
            PostDetailActivity.start(this, item)
        }

        viewModel.logoutEvent.collect(this) {
            LoginActivity.start(this)
            finish()
        }

        viewModel.resignEvent.collect(this) {
            LoginActivity.start(this)
            finish()
        }

        viewModel.loadErrorEvent.collect(this) { tr ->
            when (tr) {
                is InvalidUserException -> LoginActivity.start(this@MainActivity)
                is FirebaseFirestoreException -> {
                    if (FirebaseFirestoreException.Code.PERMISSION_DENIED == tr.code) {
                        Toast.makeText(this, getString(R.string.setting_logout_complete), Toast.LENGTH_SHORT).show()
                    }
                }

                else -> Toast.makeText(this, tr.message, Toast.LENGTH_SHORT).show()
            }
        }


        setContent {
            BloggerTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
//        viewModel.composeEvent.observe {
//            MainComposeActivity.start(this)
//        }
//
//        viewModel.openDetailEvent.observe { item ->
//            PostDetailActivity.start(this, item)
//        }
//
//        viewModel.logoutEvent.observe {
//            LoginActivity.start(this)
//            finish()
//        }
//
//        viewModel.resignEvent.observe {
//            LoginActivity.start(this)
//            finish()
//        }
//
//        viewModel.loadErrorEvent.observe { tr ->
//            when (tr) {
//                is InvalidUserException -> LoginActivity.start(this@MainActivity)
//                is FirebaseFirestoreException -> {
//                    if (FirebaseFirestoreException.Code.PERMISSION_DENIED == tr.code) {
//                        Toast.makeText(this, getString(R.string.setting_logout_complete), Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                else -> Toast.makeText(this, tr.message, Toast.LENGTH_SHORT).show()
//            }
//        }

        MainScreen(presenter = viewModel)
    }
}

