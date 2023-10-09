package com.mj.blogger.common.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
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