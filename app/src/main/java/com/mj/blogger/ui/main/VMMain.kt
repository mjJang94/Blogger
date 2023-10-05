package com.mj.blogger.ui.main

import androidx.lifecycle.ViewModel
import com.mj.blogger.ui.main.presentation.MainPresenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VMMain: ViewModel(), MainPresenter {

    private val _data = MutableStateFlow("")
    override val data: StateFlow<String> =  _data.asStateFlow()

    override fun close() {

    }
}