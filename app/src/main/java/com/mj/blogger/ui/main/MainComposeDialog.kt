package com.mj.blogger.ui.main

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.common.ktx.observe
import com.mj.blogger.ui.main.presentation.MainComposeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        super.onCreateDialog(savedInstanceState).apply {
            window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
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

        viewModel.pickImageEvent.observe {
            pickGalleryImage.launch(Unit)
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