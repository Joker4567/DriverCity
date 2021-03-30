package com.anufriev.utils.platform

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DriverState {
    private val mutableStateDriver = MutableStateFlow(-1)
    val stateDriver: StateFlow<Int> = mutableStateDriver

    fun changeStateDriver(isDriver: Int) {
        mutableStateDriver.value = isDriver
    }
}