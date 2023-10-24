package com.mj.blogger.common.compose.ktx

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.collections.immutable.toImmutableList

@Composable
fun <T> rememberImmutableList(list: List<T>) =
    remember(list) { list.toImmutableList() }