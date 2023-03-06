package com.example.supportfab.custom_fab

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GlobalFabStateManager {


	private var _mutableStateFlow = MutableStateFlow<GlobalFabState?>(null);
	val fabState = _mutableStateFlow.asStateFlow()


	fun getUpdates(): StateFlow<GlobalFabState?> {
		return fabState
	}

	fun updateState(state: GlobalFabState) {
		_mutableStateFlow.value = state
	}

	fun updatePosition(x: Float, y: Float) {
		if (_mutableStateFlow.value == null) _mutableStateFlow.value = GlobalFabState(position = FabPosition(x, y)
		) else {
			_mutableStateFlow.value = _mutableStateFlow.value?.copy(position = FabPosition(x, y))
		}
	}

	fun updateExpandState(isExpanded: Boolean) {
		if (_mutableStateFlow.value == null)
			_mutableStateFlow.value = GlobalFabState(isExpanded = isExpanded)
		else
			_mutableStateFlow.value = _mutableStateFlow.value?.copy(isExpanded = isExpanded)
	}


	fun updateIcon(icon: Int) {
		if (_mutableStateFlow.value == null)
			_mutableStateFlow.value = GlobalFabState(icon = icon)
		else
			_mutableStateFlow.value = _mutableStateFlow.value?.copy(icon = icon)
	}

}


data class GlobalFabState(
	val position: FabPosition? = null,
	val isExpanded: Boolean? = null,
	val icon: Int? = null
)

data class FabPosition(
	val x: Float,
	val y: Float
)