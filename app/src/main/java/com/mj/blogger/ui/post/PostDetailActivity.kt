package com.mj.blogger.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.post.PostDetailViewModel.PostDocumentEmptyException
import com.mj.blogger.ui.post.presenter.PostDetailScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {

    private val viewModel: PostDetailViewModel by viewModels()

    companion object {

        private const val EXTRA_POST_ID = "EXTRA_POST_ID"

        fun start(context: Context, postId: String) {
            val intent = Intent(context, PostDetailActivity::class.java).apply {
                putExtra(EXTRA_POST_ID, postId)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.getStringExtra(EXTRA_POST_ID) ?: return finish()
        viewModel.configure(data)

        setContent {
            BloggerTheme {
                PostDetailScreen()
            }
        }
    }

    @Composable
    private fun PostDetailScreen() {

        viewModel.post.observe { post ->
            Log.d(this::class.java.simpleName, "$post")
        }

        viewModel.loadErrorEvent.observe { tr ->
            val errorMsg = when (tr) {
                is PostDocumentEmptyException -> getString(R.string.detail_not_exist_posting)
                else -> tr.message
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        PostDetailScreen(presenter = viewModel)
    }
}