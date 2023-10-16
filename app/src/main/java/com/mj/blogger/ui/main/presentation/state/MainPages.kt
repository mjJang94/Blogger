package com.mj.blogger.ui.main.presentation.state

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mj.blogger.R

enum class MainPages(
    @DrawableRes val iconRes: Int,
    @StringRes val title: Int,
) {
    HOME(
        iconRes = R.drawable.ic_baseline_home,
        title = R.string.home_title,
    ),
    CREATE(
        iconRes = R.drawable.ic_baseline_create,
        title = R.string.home_create,
    ),
    SETTINGS(
        iconRes = R.drawable.ic_baseline_settings,
        title = R.string.home_settings,
    )
}