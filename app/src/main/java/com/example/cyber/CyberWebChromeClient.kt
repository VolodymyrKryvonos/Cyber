package com.example.cyber

import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.MimeTypeMap
import android.webkit.PermissionRequest
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

class CyberWebChromeClient(
    private val context: ComponentActivity
) : WebChromeClient() {

    private var permissionRequest: PermissionRequest? = null
    private var fileChooserCallback: ValueCallback<Array<out Uri>>? = null

    private val filePickerLauncher =
        context.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            fileChooserCallback?.onReceiveValue(
                if (uri != null) arrayOf(uri) else null
            )
            fileChooserCallback = null
        }
    private val permissionLauncher =
        context.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionRequest?.grant(permissionRequest?.resources)
            } else {
                permissionRequest?.deny()
            }
            permissionRequest = null
        }


    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<out Uri>>,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        fileChooserCallback = filePathCallback
        // Launch file picker for any file type or specific MIME type based on parameters
        val mimeType = convertExtensionToMimeType(fileChooserParams?.acceptTypes?.firstOrNull())
        filePickerLauncher.launch(mimeType)
        return true
    }

    fun convertExtensionToMimeType(extension: String?): String {
        val cleanExtension = extension?.trimStart('.') // Remove leading dot if present
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(cleanExtension) ?: "*/*"
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
