package cn.iwakeup.slidedrawer

import android.view.MotionEvent
import android.view.VelocityTracker
import kotlin.math.abs

interface GesturableSlideDrawer {
    enum class SlideDirection {
        LEFT_TO_RIGHT, RIGHT_TO_LEFT;
    }

    fun onScrollDrawer(distanceX: Float)


    // direction left->right 1
    // direction right -> left -1
    fun onFlingDrawer(velocityX: Float, direction: SlideDirection)

    fun onGestureFinished()
}

object GestureDelegate {

    private var touchDownX = 0f
    private var touchDownY = 0f
    private var lastX = 0f
    private var lastY = 0f

    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()


    fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var intercepted = false
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {

                touchDownX = event.x
                touchDownY = event.y
                println("onInterceptTouchEvent:ACTION_DOWN ")
                intercepted = false

            }

            MotionEvent.ACTION_MOVE -> {


                val currentX = event.x
                val currentY = event.y

                println("onInterceptTouchEvent:ACTION_MOVE")

                if (isHorizontalScrolling(touchDownX, touchDownY, currentX, currentY)) {

                    lastX = currentX
                    lastY = currentY
                    println("onInterceptTouchEvent:Drawer handled")

                    intercepted = true
                }

                lastX = currentX
                lastY = currentY


            }

        }


        return intercepted
    }


    fun onTouchEvent(gesturableSlideDrawer: GesturableSlideDrawer, event: MotionEvent): Boolean {

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

//                    updateDrawerPosition(drawerContainer.translationX + distanceX)
                    gesturableSlideDrawer.onScrollDrawer(distanceX)

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


                // isFling
                if (abs(xVelocity) > 100) {
                    val direction =
                        if (xVelocity > 0) GesturableSlideDrawer.SlideDirection.LEFT_TO_RIGHT
                        else GesturableSlideDrawer.SlideDirection.RIGHT_TO_LEFT
                    gesturableSlideDrawer.onFlingDrawer(xVelocity, direction)
                } else {
                    gesturableSlideDrawer.onGestureFinished()
                }
            }
        }

        return false
    }

    private fun isHorizontalScrolling(oldX: Float, oldY: Float, x: Float, y: Float): Boolean {
        return abs(oldX - x) > abs(oldY - y)
    }

    fun release() {
        velocityTracker.recycle()
    }
}