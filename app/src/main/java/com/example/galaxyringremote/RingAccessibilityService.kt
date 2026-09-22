package com.example.galaxyringremote

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class RingAccessibilityService : AccessibilityService() {
    companion object {
        @Volatile var instance: RingAccessibilityService? = null
        private val supportedPackages = setOf(
            "com.google.android.youtube",
            "com.instagram.android",
            "com.zhiliaoapp.musically"
        )
    }

    override fun onServiceConnected() { instance = this }
    override fun onDestroy() { if (instance === this) instance = null; super.onDestroy() }
    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit

    fun nextVideo(force: Boolean = false): Boolean {
        val pkg = rootInActiveWindow?.packageName?.toString()
        if (!force && pkg !in supportedPackages) return false
        val dm = resources.displayMetrics
        val x = dm.widthPixels * 0.5f
        val startY = dm.heightPixels * 0.78f
        val endY = dm.heightPixels * 0.28f
        val path = Path().apply { moveTo(x, startY); lineTo(x, endY) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 230))
            .build()
        return dispatchGesture(gesture, null, null)
    }
}
