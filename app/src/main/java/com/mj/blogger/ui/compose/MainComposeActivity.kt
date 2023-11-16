package com.mj.blogger.ui.compose

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import com.mj.blogger.R
import com.mj.blogger.common.base.ImageUploadFailException
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.ui.compose.presentation.MainComposeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class MainComposeActivity : AppCompatActivity() {

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

        fun start(context: Context, item: Modify? = null) {
            val intent = Intent(context, MainComposeActivity::class.java).apply {
                putExtra(EXTRA_POST_MODIFY, item)
            }
            context.startActivity(intent)
        }
    }

    private val viewModel: MainComposeViewModel by viewModels()

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

        setContent {
            BloggerTheme {
                MainComposeScreen()
            }
        }
    }

    @Composable
    private fun MainComposeScreen() {

        viewModel.pickImageEvent.observe {
            pickGalleryImage.launch(Unit)
        }

        viewModel.uploadFailEvent.observe { tr ->
            val msg = when (tr) {
                is ImageUploadFailException -> getString(R.string.compose_fail)
                else -> tr.message
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.maxImageEvent.observe {
            Toast.makeText(this, R.string.compose_posting_image_full, Toast.LENGTH_SHORT).show()
        }

        viewModel.closeEvent.observe {
            finish()
        }

        viewModel.completeEvent.observe {
            Toast.makeText(this, R.string.compose_posting_complete, Toast.LENGTH_SHORT).show()
            finish()
        }

        MainComposeScreen(presenter = viewModel)
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