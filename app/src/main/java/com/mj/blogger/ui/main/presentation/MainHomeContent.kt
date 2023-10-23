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
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.mj.blogger.R
import com.mj.blogger.common.compose.theme.BloggerTheme

@Composable
fun MainHomeContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        MainLabel()
        LoginLabel(email = "alswhddl10@naver.com")
        PostingGraphCard()
    }
}

@Composable
private fun MainLabel() {
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
private fun PostingGraphCard() {
    val labels = listOf(
        "label 1",
        "label 2",
        "label 3",
    )

    val barData = BarData().apply {
        val dataSet = BarDataSet(
            listOf(
                BarEntry(1f, 2f),
                BarEntry(2f, 4f),
                BarEntry(3f, 6f),
            ),
            "BAR DATA"
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
                    xAxis.isEnabled = false
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
                barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            }
        )
    }
}

@Composable
@Preview
private fun MainHomeContentPreview() {
    BloggerTheme {
        MainHomeContent()
    }
}