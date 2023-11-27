package com.mj.blogger.ui.compose.presentation

import android.net.Uri
import kotlinx.coroutines.flow.StateFlow

interface ComposePresenter {
    val isModify: StateFlow<Boolean>
    val title: StateFlow<String>
    val message: StateFlow<String>
    val images: StateFlow<List<Uri>>
    val imagesCount: StateFlow<Int>
    val progressing: StateFlow<Boolean>

    fun onTitleChanged(insert: String)
    fun onMessageChanged(insert: String)
    fun onPickImage()
    fun onImageCancel(index: Int)
    fun onModify()
    fun onPost()
    fun onClose()
}