package com.mobnews.app.Activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.mobnews.app.Adapter.notificationAdapter
import com.mobnews.app.DataBase.NotificationContract
import com.mobnews.app.DataBase.NotificationDbHelper
import com.mobnews.app.DataClass.notificationDataClass
import com.mobnews.app.R

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var dbHelper: NotificationDbHelper
    private lateinit var adapter: notificationAdapter
    private lateinit var image: ImageView
    private lateinit var textNoti: TextView
  //  private var nativeAd: NativeAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        image = findViewById(R.id.notifiImage)
        textNoti = findViewById(R.id.notifiText)
        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        dbHelper = NotificationDbHelper(this)

        Log.d("NotificationActivity", "Database Helper Initialized")

//        // Insert sample data if no data exists for testing
//        if (dbHelper.getAllNotifications().isEmpty()) {
//            dbHelper.insertNotification("Test Title 1", "Test Body 1", "2023-01-01")
//            dbHelper.insertNotification("Test Title 2", "Test Body 2", "2023-02-01")
//        }

        val notifications = dbHelper.getAllNotifications().reversed()

        if (notifications.isEmpty()) {
            notificationRecyclerView.visibility = View.GONE
            image.visibility = View.VISIBLE
            textNoti.visibility = View.VISIBLE
        } else {
            notificationRecyclerView.visibility = View.VISIBLE
            image.visibility = View.GONE
            textNoti.visibility = View.GONE
            loadNotifications()
        }

        findViewById<ConstraintLayout>(R.id.backBtn).setOnClickListener {
            onBackPressed()
        }
       // loadNativeAd()
    }

    private fun loadNotifications() {
        val db = dbHelper.readableDatabase
        val projection = arrayOf(
            NotificationContract.NotificationEntry.COLUMN_NAME_TITLE,
            NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE,
            NotificationContract.NotificationEntry.COLUMN_NAME_DATE
        )

        val cursor = db.query(
            NotificationContract.NotificationEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )

        val notifications = mutableListOf<notificationDataClass>()
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(NotificationContract.NotificationEntry.COLUMN_NAME_TITLE))
                val subtitle = getString(getColumnIndexOrThrow(NotificationContract.NotificationEntry.COLUMN_NAME_SUBTITLE))
                notifications.add(notificationDataClass(title, subtitle))
            }
        }
        cursor.close()
        db.close()
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = notificationAdapter(notifications)
        notificationRecyclerView.adapter = adapter
        adapter.notifyDataSetChanged()
    }

//    private fun loadNativeAd() {
//        val adLoader = AdLoader.Builder(this, "ca-app-pub-1095072040188201/4577807479")
//            .forNativeAd { ad: NativeAd ->
//                nativeAd?.destroy()
//                nativeAd = ad
//                val adView = layoutInflater.inflate(R.layout.ad_native_template, null) as NativeAdView
//                populateNativeAdView(ad, adView)
//                findViewById<FrameLayout>(R.id.ad_frame)?.apply {
//                    removeAllViews()
//                    addView(adView)
//                }
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                }
//            })
//            .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//    }
//
//    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
//        adView.headlineView = adView.findViewById(R.id.ad_headline)
//        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
//        adView.iconView = adView.findViewById(R.id.ad_app_icon)
//        adView.bodyView = adView.findViewById(R.id.ad_body)
//
//        (adView.headlineView as TextView).text = nativeAd.headline
//        (adView.bodyView as TextView).text = nativeAd.body
//        (adView.callToActionView as Button).text = nativeAd.callToAction
//
//        nativeAd.icon?.let {
//            (adView.iconView as ImageView).setImageDrawable(it.drawable)
//            adView.iconView?.visibility = View.VISIBLE
//        } ?: run {
//            adView.iconView?.visibility = View.GONE
//        }
//
//        adView.setNativeAd(nativeAd)
//    }

//    // Method to delete sample data
//    private fun deleteSampleData() {
//        dbHelper.deleteAllNotifications()
//        loadNotifications() // Reload RecyclerView after deletion
//    }

    override fun onDestroy() {
        //nativeAd?.destroy()
        super.onDestroy()
    }
}
