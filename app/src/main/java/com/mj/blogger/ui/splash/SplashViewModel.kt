package com.mj.blogger.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mj.blogger.repo.di.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: Repository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    fun waitForLoading(next: (Boolean) -> Unit) {
        viewModelScope.launch {
            val remoteUserId = auth.currentUser?.uid
            val userId = repository.userIdFlow.firstOrNull()
            Timber.d("remoteUserId = $remoteUserId, userId = $userId")
            delay(2000)
            next.invoke(!remoteUserId.isNullOrBlank() && !userId.isNullOrBlank())
        }
    }
}