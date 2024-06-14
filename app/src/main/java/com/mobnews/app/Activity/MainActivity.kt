package com.mobnews.app.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobnews.app.Fragment.DiscoverFragment
import com.mobnews.app.Fragment.HomeFragment
import com.mobnews.app.Fragment.ProfileFragment
import com.mobnews.app.R
import com.mobnews.app.Fragment.SavedFragment
import com.mobnews.app.Fragment.VideoFragment

class MainActivity : AppCompatActivity() {


    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavigationView=findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener ( navListener )

        // Display initial fragment or perform initial action
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            HomeFragment()
        ).commit()

        // Check and request notification permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }

    }
    private val navListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        var selectedFragment: Fragment? = null
        when (item.itemId) {
            R.id.navigation_home -> selectedFragment = HomeFragment()
            R.id.navigation_discover -> selectedFragment = DiscoverFragment()
            R.id.navigation_saved -> selectedFragment = SavedFragment()
            R.id.navigation_profile -> selectedFragment = ProfileFragment()
            R.id.navigation_video -> selectedFragment = VideoFragment()
        }
        // Replace the current fragment with the selected one
        if (selectedFragment != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, selectedFragment)
                .commit()
            return@OnNavigationItemSelectedListener true
        }
        false


    }
    private fun checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already granted
            // You can proceed with your logic here
        } else {
            // Request the permission
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val requestNotificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
           // initializeNotificationSettings()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()

        }
    }


}