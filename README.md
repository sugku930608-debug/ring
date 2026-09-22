# Galaxy Ring Shorts Remote v0.2

Experimental Android app: Galaxy Ring **Double Pinch → next YouTube Shorts / Instagram Reels / TikTok video**.

## What is included
- Actual BLE connection to an already-paired Galaxy Ring
- Channel 22 gesture enable command and pinch notification handling
- Android AccessibilityService vertical swipe
- Foreground ring-monitor service with reconnect
- Phone-friendly GitHub Actions APK build

## Before use
The ring must already be paired with the phone using Galaxy Wearable / Android Bluetooth.

## Phone-only build
1. Upload the extracted project contents to the root of your GitHub repository.
2. Open **Actions → Build Android APK**.
3. Tap **Run workflow** (or wait for the push build).
4. Open the finished run.
5. Download artifact **GalaxyRingRemote-debug-apk**.
6. Extract the downloaded artifact ZIP and install `app-debug.apk`.

## App setup
1. Tap **권한 허용** and allow Nearby devices/Bluetooth (and notifications if asked).
2. Tap **접근성 설정 열기** and enable `Ring Shorts Remote`.
3. Tap **Ring 모니터링 시작**.
4. When status says `Ring 연결됨 · Double Pinch 대기 중`, open Shorts/Reels/TikTok and double-pinch.

## Notes
- This is experimental and relies on a reverse-engineered Galaxy Ring BLE protocol. Samsung firmware updates can change behavior.
- Gesture monitoring may increase ring battery use. Use the **모니터링 중지** button when you do not need it.
- Some Samsung firmware may temporarily disable gesture detection; if it stops responding, stop/start monitoring once.
