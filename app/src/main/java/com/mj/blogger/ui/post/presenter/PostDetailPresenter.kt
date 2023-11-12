package com.mj.blogger.ui.post.presenter

import android.net.Uri
import com.mj.blogger.ui.post.presenter.state.PostDetail
import kotlinx.coroutines.flow.StateFlow

interface PostDetailPresenter {

    val postImages: StateFlow<List<Uri?>>
    val postItem: StateFlow<PostDetail?>

    fun onBack()
    fun onModify()
    fun onDelete()
}