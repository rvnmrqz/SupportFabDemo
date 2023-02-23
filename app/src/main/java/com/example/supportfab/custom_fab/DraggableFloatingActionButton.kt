package com.example.supportfab.custom_fab

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.OvershootInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs

open class DraggableFloatingActionButton : FloatingActionButton, OnTouchListener {

    companion object {
        private const val CLICK_DRAG_TOLERANCE = 20f // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
    }

    //callbacks
    var callback: FabCallbacks? = null

    var screenPosition = ScreenPosition.UNKNOWN
        get() {
            loadViewSpecs()
            val isOnTop = y < parentHeight - y
            val isOnLeft = x < parentWidth - x
            field = if (isOnTop) {
                if (isOnLeft) ScreenPosition.TOP_LEFT else ScreenPosition.TOP_RIGHT
            } else {
                if (isOnLeft) ScreenPosition.BOTTOM_LEFT else ScreenPosition.BOTTOM_RIGHT
            }
            return field
        }

    var dragAbilityEnabled: Boolean = true
    var sideGravityEnabled: Boolean = true

    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f

    private var viewWidth = 0
    private var viewHeight = 0
    private var parentWidth = 0
    private var parentHeight = 0

    var newX = 0f
    var newY = 0f

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val layoutParams = view.layoutParams as MarginLayoutParams
        val action = motionEvent.action

        if (action == MotionEvent.ACTION_UP && !dragAbilityEnabled) {
            callback?.onClicked(x, y)
        }

        return if (action == MotionEvent.ACTION_DOWN && dragAbilityEnabled) {
            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view.x - downRawX
            dY = view.y - downRawY
            false // not Consumed for ripple effect

        } else if (action == MotionEvent.ACTION_MOVE && dragAbilityEnabled) {
            viewWidth = view.width
            viewHeight = view.height

            val viewParent = view.parent as View
            parentWidth = viewParent.width
            parentHeight = viewParent.height

            newX = motionEvent.rawX + dX

            // Don't allow the FAB past the left hand side of the parent
            newX = layoutParams.leftMargin.toFloat().coerceAtLeast(newX)

            // Don't allow the FAB past the right hand side of the parent
            newX = (parentWidth - viewWidth - layoutParams.rightMargin).toFloat().coerceAtMost(newX)

            // Don't allow the FAB past the top of the parent
            newY = motionEvent.rawY + dY
            newY = layoutParams.topMargin.toFloat().coerceAtLeast(newY)

            // Don't allow the FAB past the bottom of the parent
            newY =
                (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat().coerceAtMost(newY)

            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            callback?.onDragStarted(newX, newY)
            true // Consumed

        } else if (action == MotionEvent.ACTION_UP) {

            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY
            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY

            if (abs(upDX) < CLICK_DRAG_TOLERANCE && abs(upDY) < CLICK_DRAG_TOLERANCE) {
                // A click
                callback?.onClicked(newX, newY)
                false // not Consumed for ripple effect

            } else { // A drag
                if (!dragAbilityEnabled) return false

                if (sideGravityEnabled) pullToSideGravity()
                else{
                    val animator = view.animate()
                        .x(newX)
                        .y(newY)
                        .setDuration(300)

                    val animatorListener = object : AnimatorListener {
                        override fun onAnimationStart(animation: Animator) {}
                        override fun onAnimationRepeat(animation: Animator) {}
                        override fun onAnimationCancel(animation: Animator) {}
                        override fun onAnimationEnd(animation: Animator) {
                            animator.setListener(null)
                            callback?.onDragCompleted(newX, newY)
                        }
                    }
                    animator
                        .setListener(animatorListener)
                        .start()
                }

                callback?.onDragStarted(newX, newY)
                false // not Consumed for ripple effect
            }
        } else {
            super.onTouchEvent(motionEvent)
        }
    }

    fun toTopRight() {
        loadViewSpecs()
        val layoutParams = this.layoutParams as MarginLayoutParams
        val requestedX = (parentWidth - viewWidth - layoutParams.rightMargin).toFloat()
        val requestedY = layoutParams.topMargin.toFloat()
        animateFabToPosition(requestedX, requestedY, 300)
    }

    fun toTopLeft() {
        loadViewSpecs()
        val layoutParams = this.layoutParams as MarginLayoutParams
        val requestedX = layoutParams.leftMargin.toFloat()
        val requestedY = layoutParams.topMargin.toFloat()
        animateFabToPosition(requestedX, requestedY, 300)
    }

    fun toBottomRight() {
        loadViewSpecs()
        val layoutParams = this.layoutParams as MarginLayoutParams
        val requestedX = (parentWidth - viewWidth - layoutParams.rightMargin).toFloat()
        val requestedY = (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat()
        animateFabToPosition(requestedX, requestedY, 300)
    }

    fun toBottomLeft() {
        loadViewSpecs()
        val layoutParams = this.layoutParams as MarginLayoutParams
        val requestedX = layoutParams.leftMargin.toFloat()
        val requestedY = (parentHeight - viewHeight - layoutParams.bottomMargin).toFloat()
        animateFabToPosition(requestedX, requestedY, 300)
    }

    fun pullToSideGravity() {
        val view = this
        val layoutParams = view.layoutParams as MarginLayoutParams

        if (sideGravityEnabled) {
            val potentialX = (parentWidth - viewWidth - layoutParams.rightMargin) / 2f
            newX = if (newX > potentialX) {
                (parentWidth - viewWidth - layoutParams.rightMargin).toFloat()
            } else {
                layoutParams.leftMargin.toFloat()
            }
        }

        val animator = view.animate()
            .x(newX)
            .y(newY)
            .setDuration(300)

        val animatorListener = object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                animator.setListener(null)
                callback?.onDragCompleted(newX, newY)
            }
        }

        animator
            .setListener(animatorListener)
            .start()
    }

    private fun animateFabToPosition(x: Float, y: Float, duration: Long = 0) {
        val animator = animate()
            .x(x)
            .y(y)
            .setInterpolator(OvershootInterpolator())
            .setDuration(duration)

        val animatorListener: AnimatorListener = object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}

            override fun onAnimationEnd(animation: Animator) {
                animator.setListener(null)
                newX = x
                newY = y
            }

            override fun onAnimationCancel(animation: Animator) {
                animator.setListener(null)
            }

            override fun onAnimationRepeat(animation: Animator) {}
        }
        animator
            .setListener(animatorListener)
            .start()
    }

    private fun loadViewSpecs() {
        val viewParent = this.parent as View
        parentWidth = viewParent.width
        parentHeight = viewParent.height
        viewWidth = this.width
        viewHeight = this.height
    }
}

enum class ScreenPosition {
    UNKNOWN, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
}

interface FabCallbacks {
    fun onDragStarted(x: Float, y: Float)
    fun onDragCompleted(x: Float, y: Float)
    fun onClicked(x: Float, y: Float)
}