package com.mobnews.app.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mobnews.app.R
import com.mobnews.app.Fragment.SearchFragment

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // Display initial fragment or perform initial action
        supportFragmentManager.beginTransaction().replace(
            R.id.searchContainer,
            SearchFragment()
        ).commit()
    }
}