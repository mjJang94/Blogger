package com.mj.blogger.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects
import com.mj.blogger.common.compose.ktx.invoke
import com.mj.blogger.repo.di.Repository
import com.mj.blogger.ui.main.presentation.MainPresenter
import com.mj.blogger.ui.main.presentation.PostingData
import com.mj.blogger.ui.main.presentation.state.MainPage
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

    private val tag = this::class.java.simpleName

    class InvalidUserException: Exception()

    init {
        viewModelScope.launch {
            runCatching {
                val collectionPath = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }
                fireStore.collection(collectionPath).addSnapshotListener { documents, exception ->
                    when {
                        exception != null -> {
                            loadError(exception)
                            return@addSnapshotListener
                        }

                        else -> {
                            val postings = documents?.toObjects<PostingData>() ?: emptyList()
                            Log.d(tag, "posting = $postings")

                            addPostingItems(postings)
                        }
                    }
                }
            }.getOrElse { tr ->
                loadError(tr)
            }
        }
    }

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

    private val _postingItems = MutableStateFlow<List<PostingData>>(emptyList())
    override val postingItems = _postingItems.asStateFlow()
    private fun addPostingItems(items: List<PostingData>) {
        viewModelScope.launch {
            _postingItems.emit(items)
        }
    }

    private val _loadErrorEvent = MutableSharedFlow<Throwable>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()
    private fun loadError(tr: Throwable) {
        viewModelScope.launch {
            _loadErrorEvent.emit(tr)
        }
    }

    private val _composeEvent = MutableSharedFlow<Unit>()
    val composeEvent = _composeEvent.asSharedFlow()
}