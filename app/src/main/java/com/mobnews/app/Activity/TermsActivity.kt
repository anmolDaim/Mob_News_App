package com.mobnews.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobnews.app.R

class TermsActivity : AppCompatActivity() {
    lateinit var backBtn:ConstraintLayout
    lateinit var termsAndConditionsWebView:WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms)
        backBtn=findViewById(R.id.backBtn)
        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        termsAndConditionsWebView=findViewById(R.id.termsAndConditionsWebView)

        val url = "https://mobnews.app/terms_n_condition.html"

        // Initialize WebView
        val webSettings: WebSettings = termsAndConditionsWebView.settings

        // Enable JavaScript
        webSettings.javaScriptEnabled = true

        // Set WebViewClient to handle links within the WebView
        termsAndConditionsWebView.webViewClient = WebViewClient()

        // Set WebChromeClient to show progress
        termsAndConditionsWebView.webChromeClient = WebChromeClient()

        // Load the URL into the WebView
        if (url != null) {
            termsAndConditionsWebView.loadUrl(url)
        }
    }
}