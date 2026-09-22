# Galaxy Ring Shorts Remote v2.1

Galaxy Ring **Double Pinch -> next Shorts/Reels/TikTok** Android prototype.

## v2.1 changes
- New package id: `com.galaxyring.remote` to avoid package/signature conflicts.
- `minSdk` lowered to 28; runtime Bluetooth permissions are version-aware.
- Manifest cleaned up for newer Samsung/Android devices.
- GitHub Actions now checks that the APK exists, prints SHA-256, and verifies the APK signature before uploading it.
- Artifact name: `GalaxyRingRemote-v2.1-debug-apk`.

## Phone-only build
1. Upload this project's **contents** to the root of your GitHub repository.
2. Open **Actions -> Build Android APK -> Run workflow**.
3. Open the completed run and download **GalaxyRingRemote-v2.1-debug-apk**.
4. Extract the artifact ZIP and install `app-debug.apk`.
5. Grant Nearby devices/Bluetooth permission.
6. Enable **Ring Shorts Remote** under Android Accessibility settings.
7. Start Ring monitoring in the app.
8. Open YouTube Shorts / Instagram Reels / TikTok and double-pinch.

## Samsung installation note
If Android still says only **"App not installed"** even though installation from the browser/My Files is allowed, check Samsung **Settings -> Security and privacy -> Auto Blocker**. Auto Blocker can reject sideloaded APKs separately from the normal "Install unknown apps" permission. Turn it off temporarily only for this test, install the APK, then you can turn it back on.

## Important
- The Galaxy Ring must already be paired with the phone.
- BLE gesture handling is experimental and may change with Samsung firmware updates.
- Gesture monitoring may increase ring battery usage; stop monitoring when it is not needed.

## Open-source attribution
The Galaxy Ring BLE protocol approach is adapted from SamsungOpenRing by TheVellichor under the MIT License. See `THIRD_PARTY_LICENSES.md`.
