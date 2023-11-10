package com.mj.blogger.ui.main.presentation.state

import android.net.Uri

data class PostingItem(
    val postId: String,
    val title: String,
    val message: String,
    val postTime: Long,
    val thumbnail: Uri?,
    val images: List<Uri?>,
)
