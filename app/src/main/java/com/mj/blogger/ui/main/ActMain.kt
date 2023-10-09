package com.mj.blogger.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.MainScreen

class ActMain : ComponentActivity() {

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, ActMain::class.java)
            context.startActivity(intent)
        }
    }

    private val viewModel: VMMain by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BloggerTheme {
                MainScreen(presenter = viewModel)
            }
        }
    }
}

