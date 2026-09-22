package com.galaxyring.remote.ble

import java.util.UUID

object RingProtocol {
    val CCCD_UUID: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    private const val GESTURE_CHANNEL: Byte = 0x16
    val ENABLE_GESTURES = byteArrayOf(GESTURE_CHANNEL, GESTURE_CHANNEL, 0x00)
    val DISABLE_GESTURES = byteArrayOf(GESTURE_CHANNEL, GESTURE_CHANNEL, 0x01)

    fun isPinch(value: ByteArray): Boolean =
        value.size >= 3 && value[0] == GESTURE_CHANNEL && value[1] == GESTURE_CHANNEL && value[2] == 0x02.toByte()
}
