package com.galaxyring.remote.ble

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Context
import android.os.Handler
import android.os.Looper
import java.util.ArrayDeque

class GalaxyRingClient(
    private val context: Context,
    private val onStatus: (String) -> Unit,
    private val onPinch: () -> Unit
) {
    private var gatt: BluetoothGatt? = null
    private var tx: BluetoothGattCharacteristic? = null
    private val pending = ArrayDeque<BluetoothGattCharacteristic>()
    private val handler = Handler(Looper.getMainLooper())
    private var stopped = false

    @SuppressLint("MissingPermission")
    fun connect() {
        stopped = false
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = manager.adapter
        if (adapter == null || !adapter.isEnabled) { onStatus("Bluetooth가 꺼져 있습니다"); return }
        val ring = adapter.bondedDevices.firstOrNull { it.name?.contains("Galaxy Ring", ignoreCase = true) == true }
        if (ring == null) { onStatus("페어링된 Galaxy Ring을 찾지 못했습니다"); return }
        onStatus("${ring.name} 연결 중…")
        gatt = ring.connectGatt(context, false, callback, BluetoothDevice.TRANSPORT_LE)
    }

    @SuppressLint("MissingPermission")
    fun stop() {
        stopped = true
        handler.removeCallbacksAndMessages(null)
        try { write(RingProtocol.DISABLE_GESTURES) } catch (_: Exception) {}
        try { gatt?.disconnect() } catch (_: Exception) {}
        try { gatt?.close() } catch (_: Exception) {}
        gatt = null; tx = null
    }

    @SuppressLint("MissingPermission")
    private fun write(bytes: ByteArray) {
        val c = tx ?: return
        val g = gatt ?: return
        c.writeType = BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            g.writeCharacteristic(c, bytes, BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE)
        } else {
            @Suppress("DEPRECATION")
            run { c.value = bytes; g.writeCharacteristic(c) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun subscribeNext(g: BluetoothGatt) {
        val c = if (pending.isEmpty()) null else pending.removeFirst()
        if (c == null) {
            onStatus("Ring 연결됨 · Double Pinch 대기 중")
            write(RingProtocol.ENABLE_GESTURES)
            return
        }
        g.setCharacteristicNotification(c, true)
        val d = c.getDescriptor(RingProtocol.CCCD_UUID)
        if (d == null) { subscribeNext(g); return }
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            g.writeDescriptor(d, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
        } else {
            @Suppress("DEPRECATION")
            run { d.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE; g.writeDescriptor(d) }
        }
    }

    private val callback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED && status == BluetoothGatt.GATT_SUCCESS) {
                onStatus("BLE 연결됨 · 서비스 확인 중…")
                g.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                tx = null
                if (!stopped) {
                    onStatus("Ring 연결 끊김 · 5초 후 재연결")
                    handler.postDelayed({ if (!stopped) connect() }, 5000)
                }
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            if (status != BluetoothGatt.GATT_SUCCESS) { onStatus("GATT 서비스 확인 실패 ($status)"); return }
            tx = g.services.asSequence().flatMap { it.characteristics.asSequence() }
                .firstOrNull { c ->
                    val svc = c.service.uuid.toString()
                    (svc.startsWith("00001b1b") || svc.startsWith("00001b1a")) &&
                        c.properties and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE != 0
                }
            if (tx == null) { onStatus("Ring 쓰기 채널을 찾지 못했습니다"); return }
            pending.clear()
            g.services.flatMap { it.characteristics }
                .filter { it.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY != 0 }
                .forEach { pending.add(it) }
            subscribeNext(g)
        }

        override fun onDescriptorWrite(g: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            subscribeNext(g)
        }

        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic, value: ByteArray) {
            if (RingProtocol.isPinch(value)) onPinch()
        }

        @Deprecated("API < 33")
        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            @Suppress("DEPRECATION") val value = characteristic.value ?: return
            if (RingProtocol.isPinch(value)) onPinch()
        }
    }
}
