package com.mj.blogger.ui.post

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.collect
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.common.ktx.toast
import com.mj.blogger.ui.compose.ComposeActivity
import com.mj.blogger.ui.main.MainActivity.Companion.EXTRA_MODIFY_DATA
import com.mj.blogger.ui.post.PostDetailViewModel.PostDetailEvent.Back
import com.mj.blogger.ui.post.PostDetailViewModel.PostDetailEvent.DeleteComplete
import com.mj.blogger.ui.post.PostDetailViewModel.PostDetailEvent.DeleteError
import com.mj.blogger.ui.post.PostDetailViewModel.PostDetailEvent.Modify
import com.mj.blogger.ui.post.presentation.PostDetailScreen
import com.mj.blogger.ui.post.presentation.state.PostDetail
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {

    private val viewModel: PostDetailViewModel by viewModels()

    companion object {

        private const val EXTRA_POST_DETAIL_ITEM = "EXTRA_POSTING_ITEM"

        fun contract(context: Context, item: PostDetail): Intent =
            Intent(context, PostDetailActivity::class.java).apply {
                putExtra(EXTRA_POST_DETAIL_ITEM, item)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(this) {
            resultFinish()
        }

        val data = intent.parcelable<PostDetail>(EXTRA_POST_DETAIL_ITEM) ?: return finish()
        viewModel.configure(data)

        viewModel.postDetailEvent.collect(this) { event ->
            when (event) {
                is Modify -> {
                    val modify = ComposeActivity.Modify(
                        postId = event.postId,
                        title = event.title,
                        message = event.message,
                        images = event.images,
                        hits = event.hits,
                    )
                    val intent = Intent().apply { putExtra(EXTRA_MODIFY_DATA, modify) }
                    setResult(RESULT_OK, intent)
                    finish()
                }

                is DeleteError -> {
                    toast(getString(R.string.detail_delete_failure))
                }

                is DeleteComplete -> {
                    toast(getString(R.string.detail_delete_complete))
                    resultFinish()
                }

                is Back -> {
                    resultFinish()
                }
            }
        }

        setContent {
            BloggerTheme {
                PostDetailScreen(presenter = viewModel)
            }
        }
    }

    private fun resultFinish(){
        setResult(RESULT_OK)
        finish()
    }
}