package com.example.supportfab

import android.app.Application
import android.util.Log
import com.example.supportfab.custom_fab.FabPosition
import com.example.supportfab.custom_fab.GlobalFabStateManager
import com.example.supportfab.custom_fab.SupportFabSharedPropertyController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "App"

class App : Application() {

	var globalFabStateManager: GlobalFabStateManager
		private set

	companion object {
		lateinit var instance: App
	}

	init {
		instance = this
		globalFabStateManager = GlobalFabStateManager()
	}

}