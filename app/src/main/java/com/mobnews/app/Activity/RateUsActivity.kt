package com.mobnews.app.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.mobnews.app.R

class RateUsActivity : AppCompatActivity() {
    lateinit var backBtn:ConstraintLayout
    lateinit var rateUsBtn: AppCompatButton
    private var nativeAd: NativeAd? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rate_us)
        backBtn=findViewById(R.id.backBtn)
        rateUsBtn=findViewById(R.id.rateUsBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        rateUsBtn.setOnClickListener {
            val url = "https://play.google.com/store/apps/details?id=com.mobnews.app" // Replace this URL with your actual rate us URL

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
        loadNativeAd()

    }

    private fun loadNativeAd() {
        //val adUnitId = getString(R.string.native_ads) // Retrieve the ad unit ID from strings.xml

        val adLoader = AdLoader.Builder(this, "ca-app-pub-1095072040188201/4577807479")
            .forNativeAd { ad: NativeAd ->
                // Destroy the old ad if it's still around
                nativeAd?.destroy()

                // Store the new ad
                nativeAd = ad

                // Inflate the layout
                val adView = layoutInflater.inflate(R.layout.ad_native_template, null) as NativeAdView

                // Populate the ad view with the ad assets
                populateNativeAdView(ad, adView)

                // Add the ad view to the ad container
                findViewById<FrameLayout>(R.id.ad_frame).apply {
                    removeAllViews()
                    addView(adView)
                }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Handle the failure by logging, altering the UI, etc.
                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.bodyView = adView.findViewById(R.id.ad_body)

        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        nativeAd.icon?.let {
            (adView.iconView as ImageView).setImageDrawable(it.drawable)
            adView.iconView?.visibility = View.VISIBLE
        } ?: run {
            adView.iconView?.visibility = View.GONE
        }

        // Assign native ad object to the native view
        adView.setNativeAd(nativeAd)
    }

    override fun onDestroy() {
        nativeAd?.destroy()
        super.onDestroy()
    }

}