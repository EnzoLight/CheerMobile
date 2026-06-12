package com.cheermobile.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun LoginWebViewScreen(
    onLoginSuccess: (String) -> Unit,
    onBack: () -> Unit
) {
    // Use the MOBILE login endpoint — Authentik will redirect back to
    // cheer://auth/callback?code=... which MainActivity intercepts.
    val loginUrl = "https://cheerapi.astrum.app.br/api/auth/mobile/login" +
            "?redirect_uri=cheer%3A%2F%2Fauth%2Fcallback"

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?
                    ): Boolean {
                        val url = request?.url ?: return false

                        // When the API redirects to our deep link, extract the code
                        // and hand it back — MainActivity will call /api/auth/mobile/exchange
                        if (url.scheme == "cheer" && url.host == "auth") {
                            val code = url.getQueryParameter("code") ?: ""
                            if (code.isNotBlank()) {
                                onLoginSuccess(code)
                            } else {
                                onBack()
                            }
                            return true // we handled it, don't load in WebView
                        }

                        return false // let WebView load normally
                    }
                }
                loadUrl(loginUrl)
            }
        }
    )
}
