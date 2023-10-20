package com.mj.blogger.common.compose.ktx

import kotlinx.coroutines.flow.MutableSharedFlow

suspend operator fun MutableSharedFlow<Unit>.invoke() = emit(Unit)