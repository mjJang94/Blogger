package com.mj.blogger.ui.main.presentation.state

data class PostingItem(
    val postId: String,
    val title: String,
    val message: String,
    val postTime: Long,
)
