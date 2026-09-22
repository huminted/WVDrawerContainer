package cn.iwakeup.drawer

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Scroller
import kotlin.math.abs

class WvDrawer(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs, 0) {


    private val drawerWidth = toPx(context, 350)
    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

    lateinit var drawerContainer: ViewGroup
    lateinit var mainContainer: ViewGroup


    init {
        LayoutInflater.from(context).inflate(R.layout.drawer, this, true)
    }


    override fun onFinishInflate() {
        super.onFinishInflate()

        drawerContainer = findViewById<ViewGroup>(R.id.drawer_content_container).apply {
            layoutParams = LayoutParams(drawerWidth, LayoutParams.MATCH_PARENT)
            translationX = -drawerWidth.toFloat()
        }

        mainContainer = findViewById(R.id.main_content_container)
    }


    fun setupDrawer(drawerContent: View, mainContent: View) {

        if (drawerContent.parent == null) {
            drawerContainer.addView(drawerContent)
        }

        if (mainContent.parent == null) {
            mainContainer.addView(mainContent)
        }

    }


    var touchDownX = 0f
    var touchDownY = 0f


    var lastX = 0f
    var lastY = 0f

    val scroller = Scroller(context)


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                touchDownX = event.x
                touchDownY = event.y
                println("onInterceptTouchEvent:ACTION_DOWN ")
                return false

            }

            MotionEvent.ACTION_MOVE -> {


                val currentX = event.x
                val currentY = event.y

                println("onInterceptTouchEvent:ACTION_MOVE")

                if (isHorizontalScrolling(touchDownX, touchDownY, currentX, currentY)) {

                    lastX = currentX
                    lastY = currentY
                    println("onInterceptTouchEvent:Drawer handled")

                    return true
                }

                lastX = currentX
                lastY = currentY


            }


            MotionEvent.ACTION_UP -> {
                println("onInterceptTouchEvent:ACTION_UP")
            }
        }


        return super.onInterceptTouchEvent(event)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        velocityTracker.addMovement(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                lastX = event.x
                lastY = event.y
//                println("ACTION_DOWN")

                return true
            }

            MotionEvent.ACTION_MOVE -> {

//                println("ACTION_MOVE")
                val currentX = event.x
                val currentY = event.y
                val distanceX = currentX - lastX

                if (isHorizontalScrolling(lastX, lastY, currentX, currentY)) {

                    updateDrawerPosition(drawerContainer.translationX + distanceX)
                    lastX = currentX
                    lastY = currentY

                    return true
                }

                lastX = currentX
                lastY = currentY


            }

            MotionEvent.ACTION_UP -> {

                velocityTracker.computeCurrentVelocity(1000)
                val xVelocity = velocityTracker.xVelocity
                println("ACTION_UP:${xVelocity} ")
                velocityTracker.clear()


//                // isFling
                if (abs(xVelocity) > 100) {

                    if (xVelocity > 0) {
                        flingToExpandedPosition(xVelocity)
                    } else {
                        flingToInitialPosition(xVelocity)

                    }

                } else {
                    onScrollingComplete()
                }


            }
        }

        return false
    }


    private fun isHorizontalScrolling(oldX: Float, oldY: Float, x: Float, y: Float): Boolean {
        return abs(oldX - x) > abs(oldY - y)
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
        velocityTracker.recycle()
    }


}