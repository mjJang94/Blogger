package com.mj.blogger.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.main.presentation.MainComposeScreen

class MainComposeDialog : AppCompatDialogFragment() {

    companion object {
        fun show(fragmentManager: FragmentManager) {
            MainComposeDialog()
                .show(fragmentManager, MainComposeDialog::class.simpleName)
        }
    }

    private val viewModel: MainComposeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.Theme_Blogger)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setContent {
            BloggerTheme {
                MainComposeScreen()
            }
        }
    }

    @Composable
    private fun MainComposeScreen() {

        viewModel.closeEvent.observe {
            dismissAllowingStateLoss()
        }

        MainComposeScreen(presenter = viewModel)
    }
}