package com.example.cyber

import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cyber.ui.theme.CyberTheme
class MainActivity : ComponentActivity() {

    private lateinit var webChromeClient: CyberWebChromeClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webChromeClient = CyberWebChromeClient(this)

        window?.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        enableEdgeToEdge()

        setContent {
            CyberTheme {
                WebViewScreen(
                    webChromeClient = webChromeClient,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@Composable
fun WebViewScreen(
    webChromeClient: WebChromeClient,
    onBackPressed: () -> Unit
) {
    val backEnabled = remember { mutableStateOf(false) }
    var webView: WebView? = null

    BackHandler(enabled = backEnabled.value) {
        if (webView?.canGoBack() == true) {
            webView?.goBack()
        } else {
            onBackPressed()
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        AndroidView(
            modifier = Modifier.padding(innerPadding),
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        allowFileAccess = true
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                            backEnabled.value = view.canGoBack()
                        }
                    }

                    this.webChromeClient = webChromeClient

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    loadUrl(BuildConfig.webUrl)
                }
            },
            update = { webView = it }
        )
    }
}
