package com.mobnews.app.Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.mobnews.app.R

class RateUsActivity : AppCompatActivity() {
    lateinit var backBtn:ConstraintLayout
    lateinit var rateUsBtn: AppCompatButton
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

    }

}