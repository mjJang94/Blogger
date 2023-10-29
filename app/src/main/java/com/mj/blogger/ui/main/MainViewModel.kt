package com.mj.blogger.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
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
                val userId = withContext(Dispatchers.IO) {
                    repository.userIdFlow.firstOrNull() ?: throw InvalidUserException()
                }

                Log.e(TAG, "collectionPath = $userId")

                fireStore.collection(userId)
                    .addSnapshotListener { documents, exception ->
                        when {
                            exception != null -> {
                                Log.e(TAG, "$exception")
                                loadError(exception)
                                return@addSnapshotListener
                            }

                            else -> {
                                val postings =
                                    documents?.toObjects<Posting>()?.map { it.translate() }
                                        ?: emptyList()
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
        Log.d(TAG, "setPostingItems = $items")
        viewModelScope.launch {
            _postingItems.emit(items)
        }
    }

    private val _prevWeekDays = flow { emit(getPreviousWeekDays()) }

    override val prevWeekDays = _prevWeekDays.map { list ->
        list.map { dateToDayOfWeek(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList()
    )

    //fun main() {
    //    val originalList = mutableListOf("element1", "element2") // 2개의 요소가 있는 원래 List
    //    val desiredSize = 7 // 원하는 List의 크기
    //    val defaultElement = "default" // 추가할 기본 요소
    //
    //    // 새 List를 생성하고 기본 요소로 채웁니다.
    //    val newList = MutableList(desiredSize) { index ->
    //        if (index < originalList.size) {
    //            originalList[index] // 원래 List의 요소를 추가합니다.
    //        } else {
    //            defaultElement // 나머지는 기본 요소를 추가합니다.
    //        }
    //    }
    //
    //    println(newList) // [element1, element2, default, default, default, default, default]
    //}
    override val postingChartEntryItems = _postingItems.map { posting ->

        val days = getPreviousWeekDays()
        val weekCount = 7

        val postingItem = posting
            .sortedByDescending { it.postTime }
            .groupBy { post -> millisecondsToDateString(post.postTime) }
            .filter { (key, _) -> days.contains(key) }
            .map { (_, list) ->
                list.size
            }

        val newList = MutableList(weekCount) { index ->
            if (index < postingItem.size) {
                BarEntry(index.toFloat(), postingItem[index].toFloat())
            } else {
                BarEntry(index.toFloat(), 0f)
            }
        }
        newList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList(),
    )

    private fun dateToDayOfWeek(dateString: String): String =
        run {
            val inputFormat =
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = inputFormat.parse(dateString)

            val outputFormat = SimpleDateFormat("EEEE", Locale.getDefault())
            date?.let { outputFormat.format(it) } ?: ""
        }

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