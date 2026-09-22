package com.example.galaxyringremote

object RingGestureBridge {
    private var lastGestureAt = 0L

    fun onDoublePinch() {
        val now = System.currentTimeMillis()
        if (now - lastGestureAt < 650) return
        lastGestureAt = now
        RingAccessibilityService.instance?.nextVideo()
    }
}
