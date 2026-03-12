# Android APK Voice Commands Fix

## Problem
Voice commands ("start stream", "stop stream", "hide video", "show video") were not working in the Android APK because:

1. **Missing Android Permissions** - The AndroidManifest.xml was missing critical permissions for microphone and camera access
2. **WebView Permissions Not Configured** - The Capacitor WebView wasn't configured to grant permissions for media capture
3. **WebSocket Communication** - WebView settings weren't optimized for WebRTC and WebSocket connections

## Solution Applied

### 1. AndroidManifest.xml Updates
Added the following permissions:
- `RECORD_AUDIO` - Required for voice recognition and microphone access
- `MODIFY_AUDIO_SETTINGS` - Required for audio control
- `CAMERA` - Required for video streaming
- `ACCESS_NETWORK_STATE` - For WebSocket connection monitoring
- `ACCESS_WIFI_STATE` - For network quality monitoring
- `WAKE_LOCK` - To keep the app active during streaming

Added feature declarations:
- `android.hardware.microphone` (required=true)
- `android.hardware.camera` (required=false)
- `android.hardware.camera.autofocus` (required=false)

### 2. MainActivity.java Enhancements
Updated MainActivity to:
- Request runtime permissions for microphone and camera on app startup
- Configure WebView settings to enable:
  - JavaScript
  - DOM Storage
  - Database
  - Media playback without user gesture
- Implement WebChromeClient to handle WebView permission requests
- Auto-grant WebView permissions when Android permissions are already granted
- Handle permission request callbacks

### 3. Capacitor Configuration
Updated capacitor.config.json to:
- Enable mixed content (for development/testing)
- Enable input capture
- Enable WebView debugging for troubleshooting

## How to Build the Updated APK

### Prerequisites
- Android Studio installed
- Java JDK 11 or higher
- Android SDK 21 or higher

### Build Steps

1. **Sync Capacitor**
   ```bash
   npx cap sync android
   ```

2. **Open in Android Studio**
   ```bash
   npx cap open android
   ```

3. **Build APK**
   - In Android Studio: Build > Build Bundle(s) / APK(s) > Build APK(s)
   - Or via command line:
   ```bash
   cd android
   ./gradlew assembleDebug
   ```

4. **Install on Device**
   ```bash
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

## Testing Voice Commands

After installing the updated APK:

1. **Grant Permissions**
   - First launch will prompt for microphone and camera permissions
   - Allow all permissions for full functionality

2. **Navigate to Device Screen**
   - Open the app and navigate to /device or /platform

3. **Test Voice Commands**
   - Tap and hold the microphone button
   - Say one of these commands:
     - "start stream" - Starts video streaming to XR Vision Dock
     - "stop stream" - Stops video streaming
     - "hide video" - Hides the video preview
     - "show video" - Shows the video preview
     - "mute" - Mutes the microphone
     - "unmute" - Unmutes the microphone
     - "connect" - Connects to the server
     - "disconnect" - Disconnects from the server

4. **Verify WebSocket Connection**
   - Check if the device connects to the server
   - Verify that voice commands are sent through WebSocket
   - Confirm that streaming starts/stops based on commands

## Troubleshooting

### Voice Commands Not Recognized
- Ensure microphone permissions are granted
- Check that the device has network connectivity
- Verify the WebSocket connection is established (check connection status)
- Try speaking more clearly or closer to the microphone

### Camera/Video Not Working
- Ensure camera permissions are granted
- Check that another app isn't using the camera
- Verify the device has a working camera

### WebSocket Not Connecting
- Check network connectivity
- Verify the server URL in capacitor.config.json
- Check server logs for connection attempts
- Enable WebView debugging and check Chrome DevTools

### Enable WebView Debugging
1. Connect device via USB
2. Enable USB Debugging on the device
3. Open Chrome and navigate to: `chrome://inspect`
4. Select your app's WebView
5. Check console for errors

## Important Notes

- The app requires an active internet connection to connect to the XR Messaging server
- Voice commands use the Web Speech API, which requires microphone permissions
- Video streaming uses WebRTC, which requires both camera and microphone permissions
- First-time users must grant all permissions for full functionality
- The app connects to: `https://xr-messaging-geexbheshbghhab7.centralindia-01.azurewebsites.net`

## Voice Command Flow

1. User holds microphone button
2. Web Speech API starts listening (requires RECORD_AUDIO permission)
3. Voice input is transcribed to text
4. Text is matched against command patterns in voice.js
5. Matched commands trigger actions (connect, stream, mute, etc.)
6. Commands are sent via WebSocket to the server
7. Server relays commands to paired XR Vision Dock
8. Streaming/controls are updated in real-time

## Files Modified

- `android/app/src/main/AndroidManifest.xml` - Added permissions and features
- `android/app/src/main/java/com/oghealthcare/xrvision/MainActivity.java` - Added permission handling and WebView configuration
- `capacitor.config.json` - Added Android-specific WebView settings
