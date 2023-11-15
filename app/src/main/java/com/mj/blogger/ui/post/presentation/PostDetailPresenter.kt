package com.mj.blogger.ui.post.presentation

import android.net.Uri
import com.mj.blogger.ui.post.presentation.state.PostDetail
import kotlinx.coroutines.flow.StateFlow

interface PostDetailPresenter {

    val progressing: StateFlow<Boolean>
    val postImages: StateFlow<List<Uri?>>
    val postItem: StateFlow<PostDetail?>

    fun onBack()
    fun onModify()
    fun onDelete()
}