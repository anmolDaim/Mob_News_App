package com.mobnews.app.DataClass

import android.app.Application
import com.google.android.gms.ads.MobileAds

class Myapplication:Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the FontSizeHelper when the application starts
        FontSizeHelper.init(this)
        MobileAds.initialize(this) {}
    }

}