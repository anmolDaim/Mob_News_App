package com.mobnews.app.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.Adapter.notificationAdapter
import com.mobnews.app.DataBase.NotificationDbHelper
import com.mobnews.app.R

class NotificationActivity : AppCompatActivity() {
    private lateinit var notificationRecyclerView: RecyclerView
    private lateinit var dbHelper: NotificationDbHelper
    private lateinit var adapter: notificationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationRecyclerView = findViewById(R.id.notificationRecyclerView)
        dbHelper = NotificationDbHelper(this)
        val notifications = dbHelper.getAllNotifications().reversed()

        adapter = notificationAdapter(notifications)

        notificationRecyclerView.adapter = adapter
        notificationRecyclerView.layoutManager = LinearLayoutManager(this)

        // If you want to handle the back button in the toolbar
        findViewById<ConstraintLayout>(R.id.backBtn).setOnClickListener {
            onBackPressed()
        }
    }
}
