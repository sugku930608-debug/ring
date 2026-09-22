package com.example.galaxyringremote

import android.app.*
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.galaxyringremote.ble.GalaxyRingClient

class RingMonitorService : Service() {
    companion object {
        const val ACTION_STATUS = "com.example.galaxyringremote.STATUS"
        const val EXTRA_STATUS = "status"
        private const val CHANNEL = "ring_monitor"
        private const val ID = 41
    }

    private lateinit var client: GalaxyRingClient

    override fun onCreate() {
        super.onCreate()
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(NotificationChannel(CHANNEL, "Galaxy Ring 연결", NotificationManager.IMPORTANCE_LOW))
        startForeground(ID, notification("시작 중…"))
        client = GalaxyRingClient(this, ::status) { RingGestureBridge.onDoublePinch() }
        client.connect()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onDestroy() { client.stop(); super.onDestroy() }

    private fun status(text: String) {
        getSystemService(NotificationManager::class.java).notify(ID, notification(text))
        sendBroadcast(Intent(ACTION_STATUS).setPackage(packageName).putExtra(EXTRA_STATUS, text))
    }

    private fun notification(text: String): Notification {
        val pi = PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Builder(this, CHANNEL)
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setContentTitle("Ring Shorts Remote")
            .setContentText(text)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }
}
