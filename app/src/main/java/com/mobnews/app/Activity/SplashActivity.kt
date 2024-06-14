package com.mobnews.app.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.mobnews.app.DataClass.FontSizeHelper
import com.mobnews.app.R

class SplashActivity : AppCompatActivity() {
    lateinit var version:TextView
    private var mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        version=findViewById(R.id.version)
        MobileAds.initialize(this) {}

        FontSizeHelper.init(this)

        // Get the version name of the current app
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName

        // Set the version name as the text of the version TextView
        version.text = "Version: $versionName"

        // Check if the user is already logged in
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            // If the user is logged in, navigate to the main activity
            navigateToMainActivity()
        } else {
            // If the user is not logged in, navigate to the login/register activity after a delay
            Handler().postDelayed({
                navigateToLoginRegisterActivity()
            }, 3000)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToLoginRegisterActivity() {
        val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}
