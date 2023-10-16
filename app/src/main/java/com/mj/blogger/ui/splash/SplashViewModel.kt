package com.mj.blogger.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.blogger.repo.di.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    repository: Repository,
) : ViewModel() {

    private val _userId = repository.userIdFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = null
    )

    fun waitForLoading(action: (Boolean) -> Unit) {
        viewModelScope.launch {
            val result = when (_userId.firstOrNull()) {
                null -> false
                else -> true
            }
            delay(3000)
            action.invoke(result)
        }
    }
}