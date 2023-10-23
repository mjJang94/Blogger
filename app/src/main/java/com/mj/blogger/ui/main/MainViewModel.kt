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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val fireStore: FirebaseFirestore,
    private val repository: Repository,
) : ViewModel(), MainPresenter {

    private val tag = this::class.java.simpleName

    fun fetchDataFromFireStoreRealtime() = viewModelScope.launch {
        val userId = repository.userId()
        Log.d(tag, "posting = id : $userId")

        fireStore.collection(userId).addSnapshotListener { documents, exception ->
            if (exception != null) {
                loadError(exception)
                return@addSnapshotListener
            }

            if (documents != null) {
                val postings = documents.toObjects<PostingData>()
                postings.forEach {
                    Log.d(tag, "posting = $it")
                }
            } else {
                Log.d(tag, "posting = null")
            }
        }
    }

    private val _loadErrorEvent = MutableSharedFlow<Exception>()
    val loadErrorEvent = _loadErrorEvent.asSharedFlow()
    private fun loadError(exception: Exception) {
        viewModelScope.launch {
            _loadErrorEvent.emit(exception)
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

    private val _composeEvent = MutableSharedFlow<Unit>()
    val composeEvent = _composeEvent.asSharedFlow()
    override fun onComposePosting() {
        viewModelScope.launch { _composeEvent() }
    }
}