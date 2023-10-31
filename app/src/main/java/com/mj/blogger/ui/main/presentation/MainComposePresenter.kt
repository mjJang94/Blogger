package com.mj.blogger.ui.main.presentation

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface MainComposePresenter {
    val title: StateFlow<String>
    val message: StateFlow<String>
    val images: StateFlow<List<Uri>>
    val imageWithPosition: StateFlow<Pair<Int, Uri>?>

    fun onTitleChanged(insert: String)
    fun onMessageChanged(insert: String)
    fun onPickImage()
    fun onPost()
    fun onClose()
}