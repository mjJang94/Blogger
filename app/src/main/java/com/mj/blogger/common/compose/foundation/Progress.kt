package com.mj.blogger.common.compose.foundation

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.mj.blogger.R

@Composable
fun CircularProgress(loading: Boolean) {
    if (loading) {
        CircularProgressIndicator(
            color = colorResource(id = R.color.purple_200),
            strokeWidth = 2.dp
        )
    }
}