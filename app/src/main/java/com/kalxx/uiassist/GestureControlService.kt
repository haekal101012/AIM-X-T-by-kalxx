package com.kalxx.uiassist
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class GestureControlService : AccessibilityService() {
    override fun onServiceConnected() { instance = this }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
    fun simulateTouch(targetX: Float, targetY: Float) {
        val path = Path()
        val startX = resources.displayMetrics.widthPixels / 2f
        val startY = resources.displayMetrics.heightPixels / 2f
        path.moveTo(startX, startY)
        path.lineTo(targetX, targetY)
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(path, 0, 40))
        dispatchGesture(gestureBuilder.build(), null, null)
    }
    companion object { var instance: GestureControlService? = null }
}
