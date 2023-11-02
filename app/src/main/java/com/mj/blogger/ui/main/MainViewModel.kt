package com.mj.blogger.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.common.firebase.vo.Posting
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.state.MainPage
import com.mj.blogger.ui.main.presentation.state.PostingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val repository: Repository,
) : ViewModel(), MainPresenter {

    companion object {
        private val TAG = this::class.java.simpleName
        private const val MAXIMUM_LAST_POST_COUNT = 10
    }

    class InvalidUserException : Exception()

    init {
        viewModelScope.launch {
            runCatching {
                val userId = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }

                fireStore.collection(userId)
                    .orderBy("postTime")
                    .addSnapshotListener { documents, exception ->
                        when {
                            exception != null -> {
                                Log.e(TAG, "$exception")
                                loadError(exception)
                                return@addSnapshotListener
                            }

                            else -> {
                                val postings = documents?.toObjects<Posting>()?.map { it.translate() } ?: emptyList()
                                Log.d(TAG, "postings = $postings")
                                setPostingItems(postings)
                            }
                        }
                    }
            }.getOrElse { tr ->
                Log.e(TAG, "$tr")
                loadError(tr)
            }
        }
    }

    private fun Posting.translate() = PostingItem(
        postId = this.postId,
        title = this.title,
        message = this.message,
        postTime = this.postTime,
    )

    private val _page = MutableStateFlow(MainPage.HOME)
    override val page = _page.asStateFlow()
    override fun onPageSwitch(page: MainPage) {
        viewModelScope.launch {
            when (page) {
                MainPage.COMPOSE -> _composeEvent()
                else -> _page.emit(page)
            }
        }
    }

    override val email = repository.emailFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = "",
    )

    private val _postingItems = MutableStateFlow<List<PostingItem>>(emptyList())
    private fun setPostingItems(items: List<PostingItem>) {
        viewModelScope.launch {
            _postingItems.emit(items)
        }
    }

    override val recentPostingItems = _postingItems
        .take(MAXIMUM_LAST_POST_COUNT)
        .map { it.reversed() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    override val allPostingItems = _postingItems
        .map { it.reversed() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList(),
        )

    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()
    private fun loadError(tr: Throwable) {
        viewModelScope.launch {
            _loadErrorEvent.emit(tr)
        }
    }

    private val _composeEvent = MutableSharedFlow<Unit>()
    val composeEvent = _composeEvent.asSharedFlow()

    private val _openDetailEvent = MutableSharedFlow<String>()
    val openDetail = _openDetailEvent.asSharedFlow()
    override fun openDetail(postId: String) {
        viewModelScope.launch {
            _openDetailEvent.emit(postId)
        }
    }
}