package com.mj.blogger.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.login.LoginActivity
import com.mj.blogger.ui.main.MainViewModel.*
import com.mj.blogger.ui.main.presentation.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext = lifecycleScope.coroutineContext

    @Inject
    lateinit var fireStore: FirebaseFirestore

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloggerTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        viewModel.composeEvent.observe {
            MainComposeDialog.show(supportFragmentManager)
        }

        viewModel.loadErrorEvent.observe { tr ->
            when (tr) {
                is InvalidUserException -> LoginActivity.start(this@MainActivity)
                else -> Toast.makeText(this, tr.message, Toast.LENGTH_SHORT).show()
            }
        }

        MainScreen(presenter = viewModel)
    }
}

