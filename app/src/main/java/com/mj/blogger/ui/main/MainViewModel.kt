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
import com.mj.blogger.ui.main.presentation.state.PostingChartItem
import com.mj.blogger.ui.main.presentation.state.PostingItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
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
                val collectionPath = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }

                fireStore.collection(collectionPath)
                    .orderBy("postTime")
                    .addSnapshotListener { documents, exception ->
                        when {
                            exception != null -> {
                                loadError(exception)
                                return@addSnapshotListener
                            }

                            else -> {
                                val postings = documents?.toObjects<Posting>()?.map { it.translate() } ?: emptyList()
                                Log.d(TAG, "posting = $postings")

                                setPostingItems(postings)
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

    private val _prevWeekDays = flow { emit(getPreviousWeekDays()) }

    override val prevWeekDays = _prevWeekDays
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

    override val postingDateItems = combine(_postingItems, _prevWeekDays) { posting, prevDay ->
        posting.groupBy { post -> millisecondsToDateString(post.postTime) }
            .filter { (key, _) -> prevDay.contains(key) }
            .map { (day, list) ->
                PostingChartItem(day = day, count = list.size)
            }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList(),
    )

    private fun getPreviousWeekDays(): List<String> {

        val calendar = Calendar.getInstance()
        val previousWeekDates = mutableListOf<String>().apply {
            // 오늘을 추가
            add(millisecondsToDateString(calendar.timeInMillis))
        }

        // 6일 전까지의 날짜를 추가
        repeat(6) {
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            previousWeekDates.add(millisecondsToDateString(calendar.timeInMillis))
        }

        Log.d(TAG, "getPreviousWeekDays() = $previousWeekDates")

        return previousWeekDates
    }

    private fun millisecondsToDateString(milliseconds: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date(milliseconds)
        return sdf.format(date)
    }

    override val recentPostingItems = _postingItems
        .take(MAXIMUM_LAST_POST_COUNT)
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

    private fun Posting.translate() = PostingItem(
        title = this.title,
        message = this.message,
        postTime = this.postTime,
    )
}