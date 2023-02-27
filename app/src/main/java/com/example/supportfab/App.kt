package com.example.supportfab

import android.app.Application
import android.util.Log
import com.example.supportfab.custom_fab.FabPosition
import com.example.supportfab.custom_fab.SupportFabSharedPropertyController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "App"
class App : Application(), SupportFabSharedPropertyController {

    companion object {
        lateinit var instance: App
    }

    init {
        instance = this
    }


    private val _fabPosition: MutableStateFlow<FabPosition?> = MutableStateFlow(null)
    val fabPosition = _fabPosition.asStateFlow()

    private val _fabIcon: MutableStateFlow<Int?> = MutableStateFlow(null)
    val fabIcon = _fabIcon.asStateFlow()

    override fun updateFabIcon(icon: Int) {
        Log.d(TAG, "updateFabIcon: called")
        _fabIcon.value = icon
    }

    override fun updateFabPosition(position: FabPosition) {
        _fabPosition.value = position
    }
}