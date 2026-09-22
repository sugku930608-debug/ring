package com.example.galaxyringremote

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    private lateinit var statusView: TextView
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            statusView.text = intent?.getStringExtra(RingMonitorService.EXTRA_STATUS) ?: "상태 확인 중"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pad = (20 * resources.displayMetrics.density).toInt()
        val root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(pad,pad,pad,pad) }
        root.addView(TextView(this).apply {
            text = "Galaxy Ring Shorts Remote\n\nDouble Pinch → 다음 Shorts/Reels/TikTok\n\n① Nearby devices 권한 허용\n② 접근성 서비스 켜기\n③ Ring 모니터링 시작"
            textSize = 18f
        })
        statusView = TextView(this).apply { text = "대기 중"; textSize = 16f; setPadding(0,pad,0,pad) }
        root.addView(statusView)
        root.addView(Button(this).apply { text = "1. 권한 허용"; setOnClickListener { requestPermissionsIfNeeded() } })
        root.addView(Button(this).apply { text = "2. 접근성 설정 열기"; setOnClickListener { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) } })
        root.addView(Button(this).apply { text = "3. Ring 모니터링 시작"; setOnClickListener { startRing() } })
        root.addView(Button(this).apply { text = "모니터링 중지"; setOnClickListener { stopService(Intent(this@MainActivity, RingMonitorService::class.java)); statusView.text = "중지됨" } })
        root.addView(Button(this).apply { text = "다음 영상 테스트"; setOnClickListener { val ok = RingAccessibilityService.instance?.nextVideo(force = true) ?: false; Toast.makeText(this@MainActivity, if(ok) "스와이프 전송" else "접근성 서비스를 먼저 켜세요", Toast.LENGTH_SHORT).show() } })
        setContentView(root)
    }

    override fun onStart() {
        super.onStart()
        ContextCompat.registerReceiver(this, receiver, IntentFilter(RingMonitorService.ACTION_STATUS), ContextCompat.RECEIVER_NOT_EXPORTED)
    }
    override fun onStop() { unregisterReceiver(receiver); super.onStop() }

    private fun requestPermissionsIfNeeded() {
        val p = mutableListOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        if (Build.VERSION.SDK_INT >= 33) p += Manifest.permission.POST_NOTIFICATIONS
        val missing = p.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missing.isEmpty()) Toast.makeText(this, "필요 권한이 이미 허용되어 있습니다", Toast.LENGTH_SHORT).show()
        else ActivityCompat.requestPermissions(this, missing.toTypedArray(), 100)
    }

    private fun startRing() {
        val needed = listOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN)
        if (needed.any { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }) {
            Toast.makeText(this, "먼저 '권한 허용'을 눌러주세요", Toast.LENGTH_SHORT).show(); return
        }
        ContextCompat.startForegroundService(this, Intent(this, RingMonitorService::class.java))
        statusView.text = "Ring 서비스 시작 중…"
    }
}
