package com.mj.blogger.ui.main.presentation.state

import androidx.annotation.DrawableRes
import com.mj.blogger.R

enum class MainPage(
    @DrawableRes val animationRes: Int,
) {
    HOME(animationRes = R.drawable.anim_main_home,),
    COMPOSE(animationRes = R.drawable.anim_main_compose,),
    BLOG(animationRes = R.drawable.anim_main_blog,),
    SETTING(animationRes = R.drawable.anim_main_setting,)
}