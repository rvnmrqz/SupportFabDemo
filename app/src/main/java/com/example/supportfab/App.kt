package com.example.supportfab

import android.app.Application
import com.example.supportfab.custom_fab.GlobalFabStateManager

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