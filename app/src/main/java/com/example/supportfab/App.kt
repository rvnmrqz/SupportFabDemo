package com.example.supportfab

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.supportfab.custom_fab.SupportFabSharedState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }

    private val _fabState = MutableLiveData<SupportFabSharedState>()
    val fabState = _fabState as LiveData<SupportFabSharedState>

    fun updateFabPosition(x: Float, y: Float) {
        _fabState.postValue(SupportFabSharedState(x, y))
    }

}