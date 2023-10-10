package com.mj.blogger.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {

    fun waitForLoading(action: () -> Unit) {
        // 일정 시간 후에 메인 화면으로 이동
        viewModelScope.launch {
            delay(3000) // 2초 동안 로딩 화면을 표시
            action.invoke()
        }
    }
}