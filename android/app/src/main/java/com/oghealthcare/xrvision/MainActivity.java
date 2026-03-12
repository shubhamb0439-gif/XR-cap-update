package com.oghealthcare.xrvision;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Request runtime permissions for microphone and camera
        requestRuntimePermissions();

        // Configure WebView after Capacitor initializes
        configureWebView();
    }

    private void requestRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = {
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.MODIFY_AUDIO_SETTINGS
            };

            boolean needsPermission = false;
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    needsPermission = true;
                    break;
                }
            }

            if (needsPermission) {
                ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void configureWebView() {
        // Get the Capacitor WebView
        WebView webView = getBridge().getWebView();
        if (webView != null) {
            WebSettings webSettings = webView.getSettings();

            // Enable necessary WebView features
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setDatabaseEnabled(true);
            webSettings.setMediaPlaybackRequiresUserGesture(false);

            // Enable WebRTC and media features
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                webSettings.setMediaPlaybackRequiresUserGesture(false);
            }

            // Set WebChromeClient to handle permission requests
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onPermissionRequest(final PermissionRequest request) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        runOnUiThread(() -> {
                            // Grant all requested permissions automatically for WebRTC
                            String[] requestedResources = request.getResources();
                            boolean hasAudio = false;
                            boolean hasVideo = false;

                            for (String resource : requestedResources) {
                                if (resource.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                                    hasAudio = true;
                                }
                                if (resource.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                                    hasVideo = true;
                                }
                            }

                            // Check if we have Android permissions
                            boolean hasRecordAudioPermission = ContextCompat.checkSelfPermission(
                                MainActivity.this, Manifest.permission.RECORD_AUDIO
                            ) == PackageManager.PERMISSION_GRANTED;

                            boolean hasCameraPermission = ContextCompat.checkSelfPermission(
                                MainActivity.this, Manifest.permission.CAMERA
                            ) == PackageManager.PERMISSION_GRANTED;

                            // Only grant WebView permissions if Android permissions are granted
                            if ((hasAudio && hasRecordAudioPermission) ||
                                (hasVideo && hasCameraPermission)) {
                                request.grant(requestedResources);
                            } else {
                                request.deny();
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (!allGranted) {
                // Permissions not granted - you might want to show a message to the user
                android.util.Log.w("MainActivity", "Some permissions were denied. Voice commands may not work.");
            }
        }
    }
}
