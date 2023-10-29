package com.mj.blogger.common.firebase.vo

import com.google.firebase.firestore.DocumentId

data class Posting(
    @DocumentId val postId: String = "",
    val title: String = "",
    val message: String = "",
    val postTime: Long = 0L,
)
