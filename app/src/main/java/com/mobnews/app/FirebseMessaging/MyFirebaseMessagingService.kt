package com.mobnews.app.FirebseMessaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mobnews.app.Activity.MainActivity
import com.mobnews.app.DataBase.NotificationDbHelper
import com.mobnews.app.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Check notification permission (if necessary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.w("FirebaseMessaging", "Notification permission not granted.")
                return
            }
        }

        // Extract notification details
        val title = message.notification?.title ?: "No Title"
        val subtitle = message.notification?.body ?: "No Body"
        val date = "4.04.2024" // Replace with your actual date logic

        Log.d("FirebaseMessaging", "Title: $title, Subtitle: $subtitle, Date: $date")

        // Store the notification data into SQLite database
        val dbHelper = NotificationDbHelper(applicationContext)
        try {
            dbHelper.insertNotification(title, subtitle, date)
        } catch (e: Exception) {
            Log.e("FirebaseMessaging", "Database insertion failed: ${e.message}")
        }

        // Display notification
        message.notification?.let {
            sendNotification(it.title ?: "", it.body ?: "")
        }
    }

    private fun sendNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since Android Oreo, notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for FCM notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = System.currentTimeMillis().toInt() // Unique ID for each notification
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}
