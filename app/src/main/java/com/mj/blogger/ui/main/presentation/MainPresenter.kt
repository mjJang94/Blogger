package com.mj.blogger.ui.main.presentation

import com.github.mikephil.charting.data.BarEntry
import com.mj.blogger.ui.main.presentation.state.MainPage
import com.mj.blogger.ui.main.presentation.state.PostingItem
import kotlinx.coroutines.flow.StateFlow
import com.mj.blogger.ui.main.presentation.state.MainPage as Page

interface MainPresenter {
    val page: StateFlow<Page>

    val email: StateFlow<String>
    val prevWeekDays: StateFlow<List<String>>
    val postingChartEntryItems: StateFlow<List<BarEntry>>

    val recentPostingItems : StateFlow<List<PostingItem>>
    val allPostingItems : StateFlow<List<PostingItem>>

    fun onPageSwitch(page: MainPage)
}