package com.mobnews.app.DataClass

import android.content.Context
import android.util.DisplayMetrics

object FontSizeHelper {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context
    }

    fun setFontSize(fontSize: Float) {
        val configuration = context.resources.configuration
        configuration.fontScale = fontSize
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as android.view.WindowManager
        wm.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.fontScale * metrics.density
        context.resources.updateConfiguration(configuration, metrics)
    }

    fun resetFontSize() {
        setFontSize(1.0f)
    }
}