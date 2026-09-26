package cn.iwakeup.slidedrawer

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import kotlin.math.abs

class SlideDrawer(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs, 0),
    GesturableSlideDrawer {

    private val drawerWidth = toPx(context, 350)

    var drawerContainer: ViewGroup
    var mainContainer: ViewGroup


    val scroller = Scroller(context)


    init {
        LayoutInflater.from(context).inflate(R.layout.drawer, this, true)
        drawerContainer = findViewById<ViewGroup>(R.id.drawer_content_container).apply {
            layoutParams = LayoutParams(drawerWidth, LayoutParams.MATCH_PARENT)
            translationX = -drawerWidth.toFloat()
        }

        mainContainer = findViewById(R.id.main_content_container)
    }


    fun setDrawerContent(drawerContent: View) {
        if (drawerContent.parent == null) {
            drawerContainer.removeAllViews()
            drawerContainer.addView(drawerContent)
        }
    }

    fun setMainContent(mainContent: View) {
        if (mainContent.parent == null) {
            mainContainer.removeAllViews()
            mainContainer.addView(mainContent)
        }

    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (GestureDelegate.onInterceptTouchEvent(event)) {
            return true
        }
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return GestureDelegate.onTouchEvent(this, event)
    }


    private fun setDrawerToInitialPosition(duration: Float = 100f) {
        drawerContainer.animate()
            .translationX(-drawerWidth.toFloat())
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(duration.toLong())
            .start()


    }


    private fun setDrawerToExpandedPosition(duration: Float = 100f) {
        drawerContainer.animate().translationX(0f)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .setDuration(duration.toLong())
            .start()


    }

    private fun flingToInitialPosition(velocity: Float) {
        setDrawerToInitialPosition(computeFlingDuration(velocity))
    }


    private fun flingToExpandedPosition(velocity: Float) {
        setDrawerToExpandedPosition(computeFlingDuration(velocity))

    }


    private fun computeFlingDuration(velocity: Float): Float {
        scroller.fling(
            drawerContainer.translationX.toInt(), 0,
            velocity.toInt(), 0,
            -drawerWidth, 0,
            0, 0
        )
        val baseDuration = scroller.duration.toFloat()


        val absVelocity = abs(velocity).coerceAtLeast(1f)

        val referenceVelocity = 1000f


        val calculatedDuration = (baseDuration * (referenceVelocity / absVelocity)).toLong()

        val finalDuration = calculatedDuration.coerceIn(50, 400).toFloat()

        println("computeFlingDuration,velocity: ${velocity},baseDuration:${baseDuration},finalDuration,$finalDuration")

        return finalDuration

    }

    private fun updateDrawerPosition(translationX: Float) {
        if (translationX >= 0) {
            drawerContainer.translationX = 0f
        } else {
            drawerContainer.translationX = translationX
        }
    }


    private fun onScrollingComplete() {
        val currentTranslation = drawerContainer.translationX
        val middleTranslationX = -(drawerWidth / 2)

        if (currentTranslation < middleTranslationX) {
            setDrawerToInitialPosition()
        } else {
            setDrawerToExpandedPosition()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        GestureDelegate.release()
    }

    override fun onScrollDrawer(distanceX: Float) {
        updateDrawerPosition(drawerContainer.translationX + distanceX)
    }

    override fun onFlingDrawer(
        velocityX: Float,
        direction: GesturableSlideDrawer.SlideDirection
    ) {
        if (direction == GesturableSlideDrawer.SlideDirection.LEFT_TO_RIGHT) {
            flingToExpandedPosition(velocityX)
        } else {
            flingToInitialPosition(velocityX)
        }
    }

    override fun onGestureFinished() {
        onScrollingComplete()
    }


}