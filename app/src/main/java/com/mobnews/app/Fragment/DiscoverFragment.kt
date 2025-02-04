package com.mobnews.app.Fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.mobnews.app.Activity.ListCategoryActivity
import com.mobnews.app.Activity.SearchActivity
import com.mobnews.app.Adapter.secondCatAdapter
import com.mobnews.app.DataClass.secondCatDataClass
import com.mobnews.app.R


class DiscoverFragment : Fragment() {
    lateinit var secondCategoryRecyclerView:RecyclerView
    lateinit var moveProfile:ImageView
    lateinit var searchLIstner:ImageView
    lateinit var adView: AdView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_discover, container, false)
        secondCategoryRecyclerView=view.findViewById(R.id.secondCategoryRecyclerView)
        searchLIstner=view.findViewById(R.id.searchBtn)
        moveProfile=view.findViewById(R.id.moveProfile)
        MobileAds.initialize(requireContext()) {}
        adView = view.findViewById(R.id.adView)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        moveProfile.setOnClickListener {
            val fragment = ProfileFragment() // Create a new instance of ProfileFragment
            fragmentManager?.beginTransaction()?.apply {
                replace(R.id.container, fragment) // Use the correct container ID
                addToBackStack(null) // This adds the transaction to the back stack (optional)
                commit()
            }
        }

        searchLIstner.setOnClickListener{
            val intent=Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        val secondArr=ArrayList<secondCatDataClass>()
        secondArr.add(secondCatDataClass(R.color.brown, R.drawable.business_icon,"Business"))
        secondArr.add(secondCatDataClass(R.color.green, R.drawable.entertainment_icon,"Entertainment"))
        secondArr.add(secondCatDataClass(R.color.blue, R.drawable.general_icon,"General"))
        secondArr.add(secondCatDataClass(R.color.navyBlue, R.drawable.health_icon,"Health"))
        secondArr.add(secondCatDataClass(R.color.orange, R.drawable.science_icon,"Science"))
        secondArr.add(secondCatDataClass(R.color.pink, R.drawable.sports_icon,"Sports"))
        secondArr.add(secondCatDataClass(R.color.yellow, R.drawable.texhnology_icon,"Technology"))
        secondArr.add(secondCatDataClass(R.color.skyBlue, R.drawable.headlines,"Hot News"))

        val layoutManager = GridLayoutManager(context, 3)
        secondCategoryRecyclerView.layoutManager = layoutManager
        val adapter=secondCatAdapter(secondArr){ categoryName ->
            // Handle item click here and navigate to the appropriate fragment
            when (categoryName) {
                "Hot News" -> navigateToFragment(categoryName)
                "Business" -> navigateToFragment(categoryName)
                "Entertainment" -> navigateToFragment(categoryName)
                "General" -> navigateToFragment(categoryName)
                "Health" -> navigateToFragment(categoryName)
                "Science" -> navigateToFragment(categoryName)
                "Sports" -> navigateToFragment(categoryName)
                "Technology" -> navigateToFragment(categoryName)
            }
        }
        secondCategoryRecyclerView.adapter=adapter

    }
    private fun navigateToFragment(categoryName:String) {
        val intent = Intent(requireContext(), ListCategoryActivity::class.java)
        intent.putExtra("categoryName", categoryName)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}