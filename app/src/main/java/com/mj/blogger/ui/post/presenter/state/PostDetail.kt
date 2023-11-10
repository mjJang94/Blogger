package com.mj.blogger.ui.post.presenter.state

import android.net.Uri

data class PostDetail(
    val title: String,
    val message: String,
    val postTime: Long,
    val images: List<Uri>,
)
