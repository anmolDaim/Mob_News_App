package com.mobnews.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.mobnews.app.R

class PrivacyActivity : AppCompatActivity() {
    lateinit var privacyWebView:WebView
    lateinit var backBtn:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy)
        privacyWebView=findViewById(R.id.privacyWebView)
        backBtn=findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        val url = "https://mobnews.app/privacy_policy.html"


        val webSettings: WebSettings = privacyWebView.settings

        // Enable JavaScript
        webSettings.javaScriptEnabled = true

        // Set WebViewClient to handle links within the WebView
        privacyWebView.webViewClient = WebViewClient()

        // Set WebChromeClient to show progress
        privacyWebView.webChromeClient = WebChromeClient()

        // Load the URL into the WebView
        if (url != null) {
            privacyWebView.loadUrl(url)
        }
    }
}