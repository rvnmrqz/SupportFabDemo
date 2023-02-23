package com.example.supportfab.custom_fab


import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.view.*
import androidx.fragment.app.FragmentActivity
import com.example.supportfab.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

class ChatSupportBuilder{

    var mainFab: DraggableFloatingActionButton? = null
        private set

    var optionsContainer: LinearLayout? = null
        private set

    private var parentContainer: ViewGroup? = null

    //region margins
    private val DEFAULT_MARGIN = 16
    private var marginLeft: Int = DEFAULT_MARGIN
    private var marginTop: Int = DEFAULT_MARGIN
    private var marginRight: Int = DEFAULT_MARGIN
    private var marginBottom: Int = DEFAULT_MARGIN
    //endregion

    //region animations
    var showToTopAnimation: Animation? = null
        private set
        get() {
            return AnimationUtils.loadAnimation(mainFab?.context, R.anim.show_to_top_animation)
        }

    var showToBottomAnimation: Animation? = null
        private set
        get() {
            return AnimationUtils.loadAnimation(mainFab?.context, R.anim.show_to_bottom_animation)
        }

    var hideToBottomAnimation: Animation? = null
        private set
        get() {
            return AnimationUtils.loadAnimation(mainFab?.context, R.anim.hide_to_bottom_animation)
        }

    var hideToTopAnimation: Animation? = null
        private set
        get() {
            return AnimationUtils.loadAnimation(mainFab?.context, R.anim.hide_to_top_animation)
        }
    //endregion

    private var isDragEnabled: Boolean? = null
    private var isSideGravityEnabled: Boolean? = null

    private var isVideoChatEnabled = false
    private var isTextChatEnabled = false
    private var isVoiceCallEnabled = false
    private var isTestButtonsEnabled = false

    private var fabInitialXPosition: Float? = null
    private var fabInitialYPosition: Float? = null

    private var buildInitiated = false
    var buildComplete = false
        private set

    //callbacks
    var supportButtonOnClickCallback: SupportButtonOnClickCallback? = null
    var fabCallback: FabCallbacks? = null

    //region builder
    fun enableLiveVideoCallOption(b: Boolean = true): ChatSupportBuilder {
        isVideoChatEnabled = b
        return this
    }

    fun enableLiveTextChatOption(b: Boolean = true): ChatSupportBuilder {
        isTextChatEnabled = b
        return this
    }

    fun enableLiveVoiceCallOption(b: Boolean = true): ChatSupportBuilder {
        isVoiceCallEnabled = b
        return this
    }

    fun enableTestOptions(b: Boolean = true): ChatSupportBuilder {
        isTestButtonsEnabled = b
        return this
    }

    fun enableDrag(b: Boolean = true): ChatSupportBuilder {
        isDragEnabled = b
        return this
    }

    fun enableSideGravity(b: Boolean = true): ChatSupportBuilder {
        isSideGravityEnabled = b
        return this
    }

    fun setMargin(left: Int, top: Int, right: Int, bottom: Int): ChatSupportBuilder {
        marginLeft = left
        marginTop = top
        marginRight = right
        marginBottom = bottom

        if (buildComplete) {
            val params = (mainFab?.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(marginLeft, marginTop, marginRight, marginBottom)
            mainFab?.layoutParams = params
            mainFab?.requestLayout()
        }

        return this
    }

    fun setMargin(all: Int): ChatSupportBuilder {
        marginLeft = all
        marginTop = all
        marginRight = all
        marginBottom = all

        if (buildComplete) {
            val params = (mainFab?.layoutParams as ViewGroup.MarginLayoutParams)
            params.setMargins(marginLeft, marginTop, marginRight, marginBottom)
            mainFab?.layoutParams = params
            mainFab?.requestLayout()
        }
        return this
    }

    fun setInitialPosition(x:Float?, y:Float?){
        fabInitialXPosition = x
        fabInitialYPosition = y
    }
    //endregion

    //region view creator
    private fun createViews() {
        buildInitiated = true

        mainFab?.let {
            modifyMainFab(it)
        } ?: kotlin.run {
            createMainFab()
        }

        createSubFabContainer()

        if (isVideoChatEnabled) createVideoFab()
        if (isTextChatEnabled) createTextFab()
        if (isVoiceCallEnabled) createTalkFab()

        if (isTestButtonsEnabled) {
            createToggleDragFab()
            createToggleSideGravity()
            createTogglePositionFab()
        }

        parentContainer?.addView(optionsContainer)
        parentContainer?.post {
            buildComplete = true
        }
    }

    private fun createMainFab() {
        parentContainer?.let {
            val newFab = DraggableFloatingActionButton(it.context)
            newFab.visibility = View.GONE
            it.addView(newFab)
            modifyMainFab(newFab)
            mainFab = newFab
        }
    }

    private fun modifyMainFab(fab: DraggableFloatingActionButton) {
        isDragEnabled?.let { fab.dragAbilityEnabled = it }
        isSideGravityEnabled?.let { fab.sideGravityEnabled = it }

        //icon
        fab.setImageResource(R.drawable.ic_question)
        fab.imageTintList =
            ColorStateList.valueOf(fab.context.resources.getColor(R.color.white, null))

        //background
        fab.backgroundTintList =
            ColorStateList.valueOf(fab.context.resources.getColor(R.color.fab_color, null))

        //set width and height
        val whParams = fab.layoutParams
        whParams.width = LayoutParams.WRAP_CONTENT
        whParams.height = LayoutParams.WRAP_CONTENT
        fab.layoutParams = whParams

        //set margin
        val params = fab.layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(marginLeft, marginTop, marginRight, marginBottom)
        fab.layoutParams = params
        fab.requestLayout()

        //set initial position
        parentContainer?.let {viewGroup ->
            viewGroup.post {
                fabInitialXPosition?.let {
                    fab.x = it
                } ?: run {
                    //bottom right
                    fab.x = (viewGroup.width - fab.width - fab.marginEnd).toFloat()
                }

                fabInitialYPosition?.let {
                    fab.y = it
                } ?: run {
                    //bottom right
                    fab.y = (viewGroup.height - fab.height - fab.marginBottom).toFloat()
                }
            }
        }

        //set callback
        fab.callback = fabCallback
    }

    private fun createSubFabContainer() {
        parentContainer?.let {
            optionsContainer = LinearLayout(it.context)
            optionsContainer?.orientation = LinearLayout.VERTICAL
            optionsContainer?.layoutParams =
                LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
                )
            optionsContainer?.visibility = View.INVISIBLE
        }
    }

    private fun createVideoFab() {
        optionsContainer?.let { container ->
            val context = container.context

            val videoFab = ExtendedFloatingActionButton(context)
            videoFab.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            videoFab.setIconResource(R.drawable.ic_video)
            videoFab.setIconTintResource(R.color.white)

            //background + ripple
            videoFab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            videoFab.setRippleColorResource(R.color.white)

            //text
            videoFab.setTextColor(context.resources.getColor(R.color.white, null))
            videoFab.text = context.getString(R.string.fab_video_call)

            //add first to view parent before requesting for layoutParams
            container.addView(videoFab)

            //set margins
            val params = (videoFab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            videoFab.layoutParams = params
            videoFab.requestLayout()

            //click listener
            videoFab.setOnClickListener {
                supportButtonOnClickCallback?.onVideoCallClicked()
            }
        }
    }

    private fun createTextFab() {
        optionsContainer?.let { container ->
            val context = container.context

            val textFab = ExtendedFloatingActionButton(context)
            textFab.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            textFab.setIconResource(R.drawable.ic_chat)
            textFab.setIconTintResource(R.color.white)

            //background + ripple
            textFab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            textFab.setRippleColorResource(R.color.white)

            //text
            textFab.setTextColor(context.resources.getColor(R.color.white, null))
            textFab.text = context.getString(R.string.fab_text_chat)

            //add first to view parent before requesting for layoutParams
            container.addView(textFab)

            //set margins
            val params = (textFab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            textFab.layoutParams = params
            textFab.requestLayout()

            //click listener
            textFab.setOnClickListener {
                supportButtonOnClickCallback?.onTextChatClicked()
            }
        }
    }

    private fun createTalkFab() {
        optionsContainer?.let { container ->
            val context = container.context

            val talkFab = ExtendedFloatingActionButton(context)
            talkFab.layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            talkFab.setIconResource(R.drawable.ic_call)
            talkFab.setIconTintResource(R.color.white)

            //background + ripple
            talkFab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            talkFab.setRippleColorResource(R.color.white)

            //text
            talkFab.setTextColor(context.resources.getColor(R.color.white, null))
            talkFab.text = context.getString(R.string.fab_voice_call)

            //add first to view parent before requesting for layoutParams
            container.addView(talkFab)

            //set margins
            val params = (talkFab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            talkFab.layoutParams = params
            talkFab.requestLayout()

            //click listener
            talkFab.setOnClickListener {
                supportButtonOnClickCallback?.onVoiceCallClicked()
            }
        }
    }

    private fun createTogglePositionFab() {
        optionsContainer?.let { container ->
            val context = container.context

            val fab = ExtendedFloatingActionButton(context)
            fab.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            fab.setIconResource(R.drawable.ic_bug)
            fab.setIconTintResource(R.color.white)

            //background + ripple
            fab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            fab.setRippleColorResource(R.color.white)

            //text
            fab.setTextColor(context.resources.getColor(R.color.white, null))
            fab.text = context.getString(R.string.fab_toggle_position)

            //add first to view parent before requesting for layoutParams
            container.addView(fab)

            //set margins
            val params = (fab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            fab.layoutParams = params
            fab.requestLayout()

            //click listener
            fab.setOnClickListener {
                when (mainFab?.screenPosition) {
                    ScreenPosition.BOTTOM_RIGHT -> mainFab?.toBottomLeft()
                    ScreenPosition.BOTTOM_LEFT -> mainFab?.toTopLeft()
                    ScreenPosition.TOP_LEFT -> mainFab?.toTopRight()
                    ScreenPosition.TOP_RIGHT -> mainFab?.toBottomRight()
                    else -> {}
                }
            }
        }
    }

    private fun createToggleSideGravity() {
        optionsContainer?.let { container ->
            val context = container.context
            val fab = ExtendedFloatingActionButton(context)
            fab.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            fab.setIconResource(R.drawable.ic_bug)
            fab.setIconTintResource(R.color.white)

            //background + ripple
            fab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            fab.setRippleColorResource(R.color.white)

            //text
            fab.setTextColor(context.resources.getColor(R.color.white, null))
            fab.text = context.getString(R.string.fab_toggle_side_gravity)

            //add first to view parent before requesting for layoutParams
            container.addView(fab)

            //set margins
            val params = (fab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            fab.layoutParams = params
            fab.requestLayout()

            //click listener
            var internalToggleValue = isSideGravityEnabled ?: false
            fab.setOnClickListener {
                val newValue: Boolean

                if (isSideGravityEnabled != null) {
                    newValue = !(isSideGravityEnabled!!)
                    isSideGravityEnabled = newValue
                } else if (mainFab?.sideGravityEnabled != null) {
                    newValue = !(mainFab!!.sideGravityEnabled)
                    mainFab?.sideGravityEnabled = newValue
                } else {
                    newValue = !internalToggleValue
                    internalToggleValue = newValue
                }

                mainFab?.sideGravityEnabled = newValue
                if (newValue) mainFab?.pullToSideGravity()
            }
        }
    }

    private fun createToggleDragFab() {
        optionsContainer?.let { container ->
            val context = container.context

            val fab = ExtendedFloatingActionButton(context)
            fab.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

            //icon
            fab.setIconResource(R.drawable.ic_bug)
            fab.setIconTintResource(R.color.white)

            //background + ripple
            fab.backgroundTintList =
                ColorStateList.valueOf(context.resources.getColor(R.color.fab_color, null))
            fab.setRippleColorResource(R.color.white)

            //text
            fab.setTextColor(context.resources.getColor(R.color.white, null))
            fab.text = context.getString(R.string.fab_toggle_drag)

            //add first to view parent before requesting for layoutParams
            container.addView(fab)

            //set margins
            val params = (fab.layoutParams as LinearLayout.LayoutParams)
            params.setMargins(0, 0, 0, 10)
            fab.layoutParams = params
            fab.requestLayout()

            //click listener
            fab.setOnClickListener {
                mainFab?.dragAbilityEnabled = !(mainFab?.dragAbilityEnabled ?: false)
            }
        }
    }

    //endregion

    fun addToFragmentActivity(view: FragmentActivity) {
        //get root view
        val viewGroup = view.findViewById<ViewGroup>(android.R.id.content)
        create(viewGroup)
    }

    fun applyToFab(fab: DraggableFloatingActionButton){
        val viewGroup = fab.parent as ViewGroup
        create(viewGroup, fab)
    }

    fun addToViewGroup(viewGroup: ViewGroup) {
        parentContainer = viewGroup
        return create(viewGroup)
    }

    private fun create(
        parentViewGroup: ViewGroup,
        fab: DraggableFloatingActionButton? = null
    ){
        parentContainer = parentViewGroup
        mainFab = fab
        if (!buildInitiated && !buildComplete) createViews()
    }
}

interface SupportButtonOnClickCallback {
    fun onVideoCallClicked()
    fun onVoiceCallClicked()
    fun onTextChatClicked()
}