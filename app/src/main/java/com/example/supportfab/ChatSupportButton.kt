package com.example.supportfab


import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity

class ChatSupportButton {

    private lateinit var builder: ChatSupportBuilder

    constructor(view: FragmentActivity){
        initBuilder()
        builder.addToFragmentActivity(view)
    }

    constructor(viewGroup: ViewGroup){
        initBuilder()
        builder.addToViewGroup(viewGroup)
    }
    constructor(fab: DraggableFloatingActionButton){
        initBuilder()
        builder.applyToFab(fab)
    }

    private fun initBuilder(){
        //load CMS value here
        builder = ChatSupportBuilder()
            .setMargin(24)
            .enableSideGravity(false)
            .enableLiveVideoCallOption()
            .enableLiveTextChatOption()
            .enableLiveVoiceCallOption()

        builder.liveSupportButtonClickListener = liveSupportButtonClickListener
        builder.fabCallback = mainFabCallBack
    }

    private val mainFab: DraggableFloatingActionButton?
        get() {
            return builder.mainFab
        }

    private val optionsContainer: LinearLayout?
        get() {
            return builder.optionsContainer
        }

    private var isSupportOngoing = false
    private var isExpanded: Boolean = false
    private var isDraggedWhileOpened = false

    var sessionListener: ChatSupportSessionListener? = null

    //region animations
    private val showToTopAnimation: Animation?
        get() {
            return builder.showToTopAnimation
        }

    private val showToBottomAnimation: Animation?
        get() {
            return builder.showToBottomAnimation
        }

    private val hideToBottomAnimation: Animation?
        get() {
            return builder.hideToBottomAnimation
        }

    private val hideToTopAnimation: Animation?
        get() {
            return builder.hideToTopAnimation
        }
    //endregion

    private var liveSupportButtonClickListener : LiveSupportButtonClickListener? = null
    private val mainFabCallBack: FabCallbacks = object : FabCallbacks {
        override fun onDragStarted(x: Float, y: Float) {
            if (isExpanded) isDraggedWhileOpened = true

            //passing animated param as false, this will make fab options disappear on screen instantly without playing the animation which may take some time
            collapseOptions(false)
        }

        override fun onDragCompleted(x: Float, y: Float) {
            if (isDraggedWhileOpened) {
                isDraggedWhileOpened = false
                expandOptions()
            }
        }

        override fun onClicked(x: Float, y: Float) {
            if (isSupportOngoing) { //disable toggle options
                //todo: open active GLIA chat/call activity
            } else
                toggleOptions()
        }
    }

    private val isBuildComplete: Boolean
        get() = builder.buildComplete


    fun setSessionListener(sessionListener: ChatSupportSessionListener): ChatSupportButton{
        this.sessionListener = sessionListener
        return this
    }
    fun show(show: Boolean = true) {
        mainFab?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun toggleOptions() {
        if (isExpanded) collapseOptions()
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

interface ChatSupportSessionListener {
    fun onLiveChatSupportStarted(supportType: SupportType)
    fun onLiveChatSupportEnded()
}

enum class SupportType {
    VIDEO_CALL,
    VOICE_CALL,
    TEXT_CHAT
}