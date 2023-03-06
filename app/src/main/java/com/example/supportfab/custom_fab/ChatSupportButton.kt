package com.example.supportfab.custom_fab

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import com.example.supportfab.App
import com.example.supportfab.EngagementActivity
import com.example.supportfab.service.DummySupportService
import com.example.supportfab.service.SupportType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ChatSupportButton"

class ChatSupportButton {

	private lateinit var activity: FragmentActivity

	//region top variables
	private var builder: ChatSupportComponentBuilder? = null

	private val mainFab: DraggableFloatingActionButton?
		get() {
			return builder?.mainFab
		}

	private val optionsContainer: LinearLayout?
		get() {
			return builder?.optionsContainer
		}

	private val isBuildComplete: Boolean
		get() = builder?.buildComplete ?: false

	private var isExpanded: Boolean = false
	private var isDraggedWhileOpened = false
	//endregion

	//region constructors
	constructor(activity: FragmentActivity) {
		this.activity = activity
		this.builder = ChatSupportComponentBuilder(activity)
		loadComponentBuilder()
	}

	constructor(activity: FragmentActivity, viewGroup: ViewGroup) {
		this.builder = ChatSupportComponentBuilder(viewGroup)
		loadComponentBuilder()
	}

	constructor(activity: FragmentActivity, fab: DraggableFloatingActionButton) {
		this.builder = ChatSupportComponentBuilder(fab)
		loadComponentBuilder()
	}

	private fun loadComponentBuilder() {
		//load CMS value here
		builder?.setMargin(24)
			?.enableSideGravity(false)
			?.enableLiveVideoCallOption()
			?.enableLiveTextChatOption()
			?.enableLiveVoiceCallOption()

		//attach callbacks
		builder?.supportFabOnClickBuilderCallback = supportFabOnClickBuilderCallback
		builder?.fabCallback = mainFabCallBack

		//finally, call build function
		builder?.build()
	}
	//endregion

	//region animations
	private val showToTopAnimation: Animation?
		get() {
			return builder?.showToTopAnimation
		}

	private val showToBottomAnimation: Animation?
		get() {
			return builder?.showToBottomAnimation
		}

	private val hideToBottomAnimation: Animation?
		get() {
			return builder?.hideToBottomAnimation
		}

	private val hideToTopAnimation: Animation?
		get() {
			return builder?.hideToTopAnimation
		}
	//endregion

	//region internal callbacks
	private var supportFabOnClickBuilderCallback = object : SupportFabOnClickBuilderCallback {
		override fun onVideoCallClicked() {
			collapseOptions(false)
			DummySupportService.instance?.startEngagement(SupportType.VIDEO_CALL)
			activity.startActivity(Intent(activity, EngagementActivity::class.java))
		}

		override fun onVoiceCallClicked() {
			collapseOptions(false)
			DummySupportService.instance?.startEngagement(SupportType.VOICE_CALL)
			activity.startActivity(Intent(activity, EngagementActivity::class.java))
		}

		override fun onTextChatClicked() {
			collapseOptions(false)
			DummySupportService.instance?.startEngagement(SupportType.TEXT_CHAT)
			activity.startActivity(Intent(activity, EngagementActivity::class.java))
		}
	}

	private val mainFabCallBack: DraggableFabEventCallback = object : DraggableFabEventCallback {
		override fun onDragStarted(x: Float, y: Float) {
			if (isExpanded) {
				isDraggedWhileOpened = true
				//passing animated param as false, this will make fab options disappear on screen
				// instantly without playing the animation which may take some time
				collapseOptions(false)
			}
		}

		override fun onDragCompleted(x: Float, y: Float) {
			if (isDraggedWhileOpened) {
				isDraggedWhileOpened = false
				expandOptions()
			}

			App.instance.globalFabStateManager.updatePosition(x, y)
		}

		override fun onClicked(x: Float, y: Float) {
			if (DummySupportService.instance?.hasEngagement() == true) {
				activity.startActivity(Intent(activity, EngagementActivity::class.java))
			} else
				toggleOptions()
		}
	}
	//endregion

	fun listenToGlobalProperty(scope: CoroutineScope): ChatSupportButton {
		scope.launch {

			App.instance.globalFabStateManager.fabState.collectLatest { globalState ->
				globalState?.let { s ->

					//FAB position
					s.position?.let { position ->
						builder?.setPosition(position.x, position.y)

						mainFab?.x = position.x
						mainFab?.y = position.y
						mainFab?.requestLayout()
					}


					//FAB icon
					s.icon?.let {
						builder?.setIcon(it)
						mainFab?.setImageResource(it)
					}


					//Expanded state
					s.isExpanded?.let {
						builder?.isExpanded(it)
						toggleOptions(!it)
					}
				}
			}
		}

		return this
	}

	fun show(show: Boolean = true) {
		mainFab?.visibility = if (show) View.VISIBLE else View.GONE
	}

	private fun toggleOptions(expandState: Boolean? = isExpanded) {
		if (expandState == true) collapseOptions()
		else expandOptions()
	}

	fun expandOptions() {
		if (!isBuildComplete) return

		mainFab?.let { fab ->
			//move the sub-options container to top or bottom of the main fab
			val layoutParams = fab.layoutParams as ViewGroup.MarginLayoutParams
			val screenPosition = fab.screenPosition

			optionsContainer?.post {
				//identify FAB screen position for the sub-container position + animation
				var isOnTop = false
				when (screenPosition) {
					ScreenPosition.TOP_LEFT -> {
						isOnTop = true
						optionsContainer?.gravity = Gravity.START
						optionsContainer?.x = fab.x //0f
						optionsContainer?.y =
							fab.y + fab.height.toFloat() + layoutParams.bottomMargin.toFloat()
					}
					ScreenPosition.TOP_RIGHT -> {
						isOnTop = true
						optionsContainer?.gravity = Gravity.END
						optionsContainer?.x = (fab.x + fab.width) - (optionsContainer?.width
							?: 0) //(mainFab.parentWidth - (fabContainer?.width ?: 0)).toFloat()
						optionsContainer?.y =
							fab.y + fab.height.toFloat() + layoutParams.bottomMargin.toFloat()
					}
					ScreenPosition.BOTTOM_LEFT -> {
						optionsContainer?.gravity = Gravity.START
						optionsContainer?.x = fab.x //0f
						optionsContainer?.y = (fab.y - (optionsContainer?.height
							?: 0).toFloat() - layoutParams.topMargin.toFloat())
					}
					ScreenPosition.BOTTOM_RIGHT -> {
						optionsContainer?.gravity = Gravity.END
						optionsContainer?.x = (fab.x + fab.width) - (optionsContainer?.width
							?: 0) //(mainFab.parentWidth - (fabContainer?.width ?: 0)).toFloat()
						optionsContainer?.y =
							(fab.y - (optionsContainer?.height
								?: 0).toFloat() - layoutParams.topMargin.toFloat())
					}
					else -> {}
				}

				//animate
				optionsContainer?.visibility = View.VISIBLE
				val showAnimation = if (isOnTop) showToBottomAnimation else showToTopAnimation

				optionsContainer?.children?.forEach {
					it.visibility = View.VISIBLE
					it.startAnimation(showAnimation)
				}

				isExpanded = true
			}
		}
	}

	fun collapseOptions(animated: Boolean = true) {
		if (!isExpanded || !isBuildComplete) return

		if (animated) {
			val fabPosition = mainFab?.screenPosition
			val isOnTop =
				(fabPosition == ScreenPosition.TOP_LEFT || fabPosition == ScreenPosition.TOP_RIGHT)
			val hideAnimation = if (isOnTop) hideToTopAnimation else hideToBottomAnimation

			optionsContainer?.children?.forEach {
				it.startAnimation(hideAnimation)
			}
		} else {
			optionsContainer?.visibility = View.GONE
		}
		isExpanded = false
	}
}