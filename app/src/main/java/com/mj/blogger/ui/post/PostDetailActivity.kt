package com.mj.blogger.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.ui.compose.MainComposeActivity
import com.mj.blogger.ui.post.PostDetailViewModel.PostDetailEvent.*
import com.mj.blogger.ui.post.presentation.PostDetailScreen
import com.mj.blogger.ui.post.presentation.state.PostDetail
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

        fun intent(context: Context, item: PostDetail? = null): Intent =
            Intent(context, PostDetailActivity::class.java).apply {
                putExtra(EXTRA_POST_DETAIL_ITEM, item)
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

        viewModel.postDetailEvent.observe { event ->
            when (event) {
                is Modify -> {
                    val modify = MainComposeActivity.Modify(
                        postId = event.postId,
                        title = event.title,
                        message = event.message,
                        images = event.images,
                        hits = event.hits,
                    )
                    MainComposeActivity.start(this, modify)
                    finish()
                }

                is DeleteError -> {
                    Toast.makeText(this, getString(R.string.detail_delete_failure), Toast.LENGTH_SHORT).show()
                }

                is DeleteComplete -> {
                    Toast.makeText(this, getString(R.string.detail_delete_complete), Toast.LENGTH_SHORT).show()
                    finish()
                }

                is Back -> finish()
            }
        }

        PostDetailScreen(presenter = viewModel)
    }
}