package com.mj.blogger.ui.main.presentation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mj.blogger.R
import com.mj.blogger.common.compose.ktx.rememberImmutableList
import com.mj.blogger.common.compose.theme.BloggerTheme
import com.mj.blogger.ui.main.presentation.state.MainContentState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainHomeContent(
    state: MainContentState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        WelcomeLabel()

        LoginLabel(email = state.email)

        PostingGraphCard(
            prevWeekDayItems = rememberImmutableList(state.prevWeekDays),
            postingChartEntryItems = rememberImmutableList(state.postingChartEntryItems),
        )
    }
}

@Composable
private fun WelcomeLabel() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 58.dp),
        text = stringResource(R.string.main_greeting_label),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        textAlign = TextAlign.Start,
    )
}

@Composable
private fun LoginLabel(email: String) {
    Text(
        modifier = Modifier.wrapContentSize(),
        text = email,
        fontSize = 12.sp,
        color = Color.Black,
    )
}

@Composable
private fun PostingGraphCard(
    prevWeekDayItems: ImmutableList<String>,
    postingChartEntryItems: ImmutableList<BarEntry>,
) {

    val data = remember(postingChartEntryItems) {
        BarData(
            BarDataSet(postingChartEntryItems, "")
        ).apply {
            barWidth = 0.5f
        }
    }

//    LaunchedEffect(postingChartEntryItems) {
//        snapshotFlow {
//            BarData(
//                BarDataSet(postingChartEntryItems, "")
//            ).apply {
//                barWidth = 0.5f
//            }
//        }.collect { barData ->
//            data = barData
//        }
//    }

    Log.d("MainHomeContent", "${data.dataSets}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        elevation = 4.dp,
    ) {
        AndroidView(
            factory = { context ->
                BarChart(context).apply {
                    setTouchEnabled(false)
                    //X, Y축 숨기기
                    xAxis.apply {
                        setDrawGridLines(false)
                        isEnabled = true
                        position = XAxis.XAxisPosition.BOTTOM
                        valueFormatter = IndexAxisValueFormatter(prevWeekDayItems)
                    }

                    axisLeft.apply {
                        setDrawGridLines(false)
                        isEnabled = false
                    }

                    axisRight.isEnabled = false

                    legend.isEnabled = false

                    description = Description().apply { isEnabled = false }

                    setBackgroundColor(android.graphics.Color.TRANSPARENT)
                }
            },
            update = { barChart ->
                barChart.data = data
            }
        )
    }
}

@Composable
@Preview
private fun MainHomeContentPreview() {
    BloggerTheme {
        MainHomeContent(rememberPreviewMainContentState())
    }
}