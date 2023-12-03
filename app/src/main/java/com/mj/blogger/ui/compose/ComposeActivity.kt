package com.mj.blogger.ui.compose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.collect
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.common.ktx.toast
import com.mj.blogger.ui.compose.ComposeViewModel.*
import com.mj.blogger.ui.compose.presentation.MainComposeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class ComposeActivity : AppCompatActivity() {

    @Parcelize
    data class Modify(
        val postId: String,
        val title: String,
        val message: String,
        val hits: Int,
        val images: List<Uri>,
    ) : Parcelable

    companion object {
        private const val EXTRA_POST_MODIFY = "EXTRA_POST_MODIFY"

        fun contract(context: Context, item: Modify? = null): Intent =
            Intent(context, ComposeActivity::class.java).apply {
                putExtra(EXTRA_POST_MODIFY, item)
            }
    }

    private val viewModel: ComposeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        intent.parcelable<Modify>(EXTRA_POST_MODIFY)?.let { data ->
            viewModel.configure(
                postId = data.postId,
                title = data.title,
                message = data.message,
                hits = data.hits,
                images = data.images,
            )
        }

        viewModel.pickImageEvent.collect(this) {
            pickGalleryImage.launch(Unit)
        }

        viewModel.uploadFailEvent.collect(this) { tr ->
            val msg = when (tr) {
                is ImageUploadFailException -> getString(R.string.compose_fail)
                else -> tr.message
            }
            toast(msg)
        }

        viewModel.maxImageEvent.collect(this) {
            toast(getString(R.string.compose_posting_image_full))
        }

        viewModel.closeEvent.collect(this) {
            finish()
        }

        viewModel.completeEvent.collect(this) {
            toast(getString(R.string.compose_posting_complete))
            setResult(RESULT_OK)
            finish()
        }

        setContent {
            BloggerTheme {
                MainComposeScreen(presenter = viewModel)
            }
        }
    }



    private val pickGalleryImage = registerForActivityResult(
        object : ActivityResultContract<Unit, List<Uri>>() {
            override fun createIntent(context: Context, input: Unit): Intent =
                Intent(Intent.ACTION_GET_CONTENT).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                    addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                    putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
                }

            override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
                val resultIntent = intent.takeIf { resultCode == Activity.RESULT_OK }
                val images = mutableListOf<Uri>()
                resultIntent?.let { result ->
                    when (val clip = result.clipData) {
                        null -> {
                            val data = result.data ?: return@let
                            images.add(data)
                        }

                        else -> {
                            for (i in 0 until clip.itemCount) {
                                images.add(clip.getItemAt(i).uri)
                            }
                        }
                    }
                }
                return images
            }
        }
    ) { images ->
        if (images.isEmpty()) return@registerForActivityResult
        viewModel.imagePicked(images)
    }
}