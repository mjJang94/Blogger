package com.mj.blogger.ui.post.presentation.state

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PostDetail(
    val postId: String,
    val title: String,
    val message: String,
    val postTime: Long,
    val hits: Int,
    val images: List<Uri>,
) : Parcelable
