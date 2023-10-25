package com.mj.blogger.common.compose.ktx

import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import androidx.compose.runtime.Composable
import java.util.Date
import java.util.Locale

@Composable
fun ConvertMillisToFormattedDate(millis: Long): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(millis))
