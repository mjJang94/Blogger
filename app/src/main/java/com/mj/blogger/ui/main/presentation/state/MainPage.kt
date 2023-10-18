package com.mj.blogger.ui.main.presentation.state

import androidx.annotation.DrawableRes
import com.mj.blogger.R

enum class MainPage(
    @DrawableRes val defaultIconRes: Int,
    @DrawableRes val selectedIconRes: Int,
) {
    HOME(
        defaultIconRes = R.drawable.ic_baseline_home_off,
        selectedIconRes = R.drawable.ic_baseline_home_on,
    ),
    CREATE(
        defaultIconRes = R.drawable.ic_baseline_create_off,
        selectedIconRes = R.drawable.ic_baseline_create_on,
    ),
    SETTINGS(
        defaultIconRes = R.drawable.ic_baseline_settings_off,
        selectedIconRes = R.drawable.ic_baseline_settings_on,
    )
}