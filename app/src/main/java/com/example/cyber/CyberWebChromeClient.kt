package com.example.cyber

import android.content.pm.PackageManager
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CyberWebChromeClient(
    private val context: ComponentActivity
) : WebChromeClient() {

    private var permissionRequest: PermissionRequest? = null

    private val permissionLauncher =
        context.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionRequest?.grant(permissionRequest?.resources)
            } else {
                permissionRequest?.deny()
            }
            permissionRequest = null
        }

    override fun onPermissionRequest(request: PermissionRequest?) {
        if (request == null) {
            return
        }

        permissionRequest = request

        if (!request.resources.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
            request.deny()
            permissionRequest = null
            return
        }

        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            request.grant(request.resources)
            permissionRequest = null
        } else {
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }
}
