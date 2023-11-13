package com.mj.blogger.ui.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.common.ktx.parcelable
import com.mj.blogger.ui.main.MainComposeViewModel.*
import com.mj.blogger.ui.main.presentation.MainComposeScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.parcelize.Parcelize

@AndroidEntryPoint
class MainComposeDialog : AppCompatDialogFragment() {

    @Parcelize
    data class Modify(
        val postId: String,
        val title: String,
        val message: String,
        val images: List<Uri>,
    ) : Parcelable

    companion object {
        private const val EXTRA_POST_MODIFY = "EXTRA_POST_MODIFY"

        fun show(fragmentManager: FragmentManager, data: Modify? = null) {
            val args = Bundle().apply {
                putParcelable(EXTRA_POST_MODIFY, data)
            }
            MainComposeDialog().apply {
                arguments = args
            }.show(fragmentManager, MainComposeDialog::class.simpleName)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.parcelable<Modify>(EXTRA_POST_MODIFY)?.let { data ->
            viewModel.configure(
                postId = data.postId,
                title = data.title,
                message = data.message,
                images = data.images,
            )
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
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        viewModel.maxImageEvent.observe {
            Toast.makeText(requireContext(), R.string.compose_posting_image_full, Toast.LENGTH_SHORT).show()
        }

        viewModel.closeEvent.observe {
            dismissAllowingStateLoss()
        }

        viewModel.completeEvent.observe {
            Toast.makeText(requireContext(), R.string.compose_posting_complete, Toast.LENGTH_SHORT).show()
            dismissAllowingStateLoss()
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