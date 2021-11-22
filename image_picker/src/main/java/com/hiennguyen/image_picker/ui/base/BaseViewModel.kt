package com.hiennguyen.image_picker.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hiennguyen.image_picker.repository.ResultWrapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseViewModel<Intent : ViewIntent, State: ViewState> : ViewModel(){

    protected val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State> = _state

    protected fun launchOnUi(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch { block() }
    }

    open fun resetState() {}
    abstract fun dispatchIntent(intent: Intent)
    abstract fun <T> ResultWrapper<T>.process(intent: Intent ?= null, successFun: (T) -> State) : State
}