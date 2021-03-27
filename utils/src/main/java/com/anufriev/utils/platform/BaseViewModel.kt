package com.anufriev.utils.platform

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    val mainState = MutableLiveData<Event<State>>()

    protected fun handleState(state: State) {
        if (state is State.Error) {
            mainState.value = Event(state)
        } else
            mainState.value = Event(state)
    }

    protected fun handleStateWithExit(state: State) {
        mainState.value = Event(state)
    }

    protected fun launch(func: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.Main) { func.invoke() }

    protected fun launchIO(func: suspend () -> Unit) =
        viewModelScope.launch(Dispatchers.IO) { func.invoke() }
}
