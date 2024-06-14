package com.mobnews.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobnews.app.R

class WebViewActivity : AppCompatActivity() {
    lateinit var back:ConstraintLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url = intent.getStringExtra("url")

        // Initialize WebView
        val webView: WebView = findViewById(R.id.webView)
        val webSettings: WebSettings = webView.settings

        // Enable JavaScript
        webSettings.javaScriptEnabled = true

        // Set WebViewClient to handle links within the WebView
        webView.webViewClient = WebViewClient()

        // Set WebChromeClient to show progress
        webView.webChromeClient = WebChromeClient()

        // Load the URL into the WebView
        if (url != null) {
            webView.loadUrl(url)
        }
    }
}