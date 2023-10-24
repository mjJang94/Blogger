package com.mj.blogger.ui.main.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.collections.immutable.toImmutableList
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
import com.mj.blogger.ui.main.presentation.state.PostingChartItem
import com.mj.blogger.ui.main.presentation.state.PostingItem
import kotlinx.collections.immutable.ImmutableList

@Composable
fun MainHomeContent(
    email: String,
    prevWeekDays: List<String>,
    postingChartItems: List<PostingChartItem>,
    recentPostingItems: List<PostingItem>,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        WelcomeLabel()

        LoginLabel(email = "alswhddl10@naver.com")

        PostingGraphCard(
            prevWeekDays = rememberImmutableList(prevWeekDays),
            postingChartItems = rememberImmutableList(postingChartItems),
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
    prevWeekDays: ImmutableList<String>,
    postingChartItems: ImmutableList<PostingChartItem>,
) {

    val barData = BarData().apply {
        val dataSet = BarDataSet(
            postingChartItems.mapIndexed { index, postingChartItem ->
                BarEntry(index.toFloat(), postingChartItem.count.toFloat())
            }, "BAR DATA"
        )
        addDataSet(dataSet)
        barWidth = 0.2f
    }

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
                    xAxis.isEnabled = true
                    axisLeft.isEnabled = false
                    axisRight.isEnabled = false

                    //그리드 라인 제거
                    xAxis.setDrawGridLines(false)
                    axisLeft.setDrawGridLines(false)

                    //범례 숨기기
                    legend.isEnabled = false

                    //배경 비우기
                    setBackgroundColor(android.graphics.Color.TRANSPARENT)

                    //라벨 아래에 위치
                    xAxis.position = XAxis.XAxisPosition.BOTTOM
                }
            },
            update = { barChart ->
                barChart.data = barData
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prevWeekDays)
                barChart.description = Description().apply { isEnabled = false }
            }
        )
    }
}

@Composable
@Preview
private fun MainHomeContentPreview() {

    val recentItems = listOf(
        PostingItem(
            title = "안드로이드 활용법",
            message = "안드로이드 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis(),
        ),
        PostingItem(
            title = "파이어베이스 활용법",
            message = "파이어베이스 활용법에 대해 알아봅니다.",
            postTime = System.currentTimeMillis(),
        ),
        PostingItem(
            title = "갤럭시 활용법",
            message = "갤럭시에 대해 알아봅니다.",
            postTime = System.currentTimeMillis(),
        )

    )

    val prevWeekDays = listOf(
        "월",
        "화",
        "수",
        "목",
        "금",
        "토",
        "일",
    )

    val postingDateItems = listOf(
        PostingChartItem(
            day = "2023-10-18",
            count = 1
        ),
        PostingChartItem(
            day = "2023-10-19",
            count = 1
        ),
        PostingChartItem(
            day = "2023-10-20",
            count = 2
        ),
        PostingChartItem(
            day = "2023-10-21",
            count = 9
        ),
        PostingChartItem(
            day = "2023-10-22",
            count = 2
        ),
        PostingChartItem(
            day = "2023-10-23",
            count = 5
        ),
        PostingChartItem(
            day = "2023-10-24",
            count = 10
        ),
    )

    BloggerTheme {
        MainHomeContent(
            email = "alswhddl10@naver.com",
            prevWeekDays = prevWeekDays.toImmutableList(),
            postingChartItems = postingDateItems.toImmutableList(),
            recentPostingItems = recentItems.toImmutableList(),
        )
    }
}