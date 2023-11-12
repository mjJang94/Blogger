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
import com.mj.blogger.common.base.ConfigurationEmptyException
import com.mj.blogger.common.base.PostDocumentEmptyException
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.ui.main.presentation.state.PostingItem
import com.mj.blogger.ui.post.presenter.PostDetailScreen
import com.mj.blogger.ui.post.presenter.state.PostDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {

    private val viewModel: PostDetailViewModel by viewModels()

    companion object {

        private const val EXTRA_POST_DETAIL_ITEM = "EXTRA_POSTING_ITEM"

        fun start(context: Context, item: PostDetail) {
            val intent = Intent(context, PostDetailActivity::class.java).apply {
                putExtra(EXTRA_POST_DETAIL_ITEM, item)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val data = intent.parcelable<PostDetail>(EXTRA_POST_DETAIL_ITEM) ?: return finish()
        viewModel.configure(data)

        setContent {
            BloggerTheme {
                PostDetailScreen()
            }
        }
    }

    @Composable
    private fun PostDetailScreen() {

        viewModel.postItem.observe { post ->
            Log.d(this::class.java.simpleName, "$post")
        }

        viewModel.loadErrorEvent.observe { tr ->
            if (tr is ConfigurationEmptyException) finish()

            val errorMsg = when (tr) {
                is PostDocumentEmptyException -> getString(R.string.detail_not_exist_posting)
                else -> tr.message
            }
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
        }

        viewModel.deleteErrorEvent.observe {
            Toast.makeText(this, getString(R.string.detail_delete_failure), Toast.LENGTH_SHORT).show()
        }

        viewModel.backEvent.observe {
            finish()
        }

        viewModel.deleteEvent.observe {
            Toast.makeText(this, getString(R.string.detail_delete_complete), Toast.LENGTH_SHORT).show()
            finish()
        }

        viewModel.modifyEvent.observe {

        }

        PostDetailScreen(presenter = viewModel)
    }
}