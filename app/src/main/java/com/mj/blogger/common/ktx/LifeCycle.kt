package com.mj.blogger.common.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
inline fun <reified T> Flow<T>.observe(
    owner: LifecycleOwner = LocalLifecycleOwner.current,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>,
) {
    LaunchedEffect(Unit) {
        owner.lifecycleScope.launch {
            flowWithLifecycle(owner.lifecycle, state).collect(collector)
        }
    }
}

inline fun <reified T> Flow<T>.collect(
    owner: LifecycleOwner,
    state: Lifecycle.State = Lifecycle.State.STARTED,
    collector: FlowCollector<T>,
) {
    owner.lifecycleScope.launch {
        owner.repeatOnLifecycle(state){
            collect(collector)
        }
    }
}