package com.mobnews.app.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobnews.app.Activity.ReadingActivity
import com.mobnews.app.Adapter.ItemsAdapter
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.R

class SavedFragment : Fragment(), ItemsAdapter.OnFavoriteSelectedListener {
    private lateinit var savedArticlesRecyclerView: RecyclerView
    private lateinit var favText: TextView
    private lateinit var favImage: ImageView
    private lateinit var moveToHomePage: TextView
    private lateinit var adapter: ItemsAdapter
    private var nativeAd: NativeAd? = null
   // private lateinit var adFrameLarge: FrameLayout

    fun setData(bundle: Bundle) {
        arguments = bundle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)
        savedArticlesRecyclerView = view.findViewById(R.id.savedArticlesRecyclerView)
        favImage = view.findViewById(R.id.favImage)
        favText = view.findViewById(R.id.favText)
        moveToHomePage = view.findViewById(R.id.moveToHomePage)
       // adFrameLarge = view.findViewById(R.id.ad_frame_large)
        moveToHomePage.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView and load articles
        setupRecyclerView()

        // Load native ad
        //loadNativeAd()
    }

    private fun setupRecyclerView() {
        val gson = Gson()
        val sharedPreferences = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val articleList: MutableList<Data1.Article> = if (articleListJson != null) {
            gson.fromJson(articleListJson, type)
        } else {
            mutableListOf()
        }

        articleList.reverse()

        adapter = ItemsAdapter(requireContext(), articleList, { article ->
            val intent = Intent(requireContext(), ReadingActivity::class.java)
            intent.putExtra("title", article.title)
            intent.putExtra("publishedAt", article.publishedAt)
            intent.putExtra("author", article.author)
            intent.putExtra("urlToImage", article.urlToImage)
            intent.putExtra("content", article.content)
            intent.putExtra("urlToChrome", article.url)
            intent.putExtra("isFavorite", isArticleFavorite(article))
            startActivity(intent)
        }, this)

        savedArticlesRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        savedArticlesRecyclerView.adapter = adapter

        // Update visibility of views based on data availability
        if (articleList.isEmpty()) {
            savedArticlesRecyclerView.visibility = View.GONE
            favText.visibility = View.VISIBLE
            favImage.visibility = View.VISIBLE
            moveToHomePage.visibility = View.VISIBLE
            //adFrameLarge.visibility = View.VISIBLE
        } else {
            savedArticlesRecyclerView.visibility = View.VISIBLE
            favText.visibility = View.GONE
            favImage.visibility = View.GONE
            moveToHomePage.visibility = View.GONE
           // adFrameLarge.visibility = View.VISIBLE
        }
    }


    private fun isArticleFavorite(article: Data1.Article): Boolean {
        val sharedPreferences = requireActivity().getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val existingArticleList: MutableList<Data1.Article> = Gson().fromJson(articleListJson, type) ?: mutableListOf()
        return existingArticleList.any { it.title == article.title }
    }

//    private fun populateLargeNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
//        adView.headlineView = adView.findViewById(R.id.ad_headline_large)
//        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action_large)
//        adView.iconView = adView.findViewById(R.id.ad_app_icon_large)
//        adView.bodyView = adView.findViewById(R.id.ad_body_large)
//        adView.mediaView = adView.findViewById(R.id.ad_media_large) as MediaView
//
//        (adView.headlineView as TextView).text = nativeAd.headline
//        (adView.bodyView as TextView).text = nativeAd.body
//        (adView.callToActionView as AppCompatButton).text = nativeAd.callToAction
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

//    private fun loadNativeAd() {
//        val adLoader = AdLoader.Builder(requireContext(), "ca-app-pub-1095072040188201/4577807479")
//            .forNativeAd { ad: NativeAd ->
//                nativeAd?.destroy()
//                nativeAd = ad
//                val adView = layoutInflater.inflate(R.layout.ad_large_native, adFrameLarge, false) as NativeAdView
//                populateLargeNativeAdView(ad, adView)
//                adFrameLarge.removeAllViews()
//                adFrameLarge.addView(adView)
//            }
//            .withAdListener(object : AdListener() {
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    // Handle the failure by logging, altering the UI, etc.
//                }
//            })
//            .build()
//
//        adLoader.loadAd(AdRequest.Builder().build())
//    }

    override fun onFavoriteSelected(article: Data1.Article) {
        // Implement the logic to remove the article from favorites
        val sharedPreferences = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val articleList: MutableList<Data1.Article> = gson.fromJson(articleListJson, type)

        val iterator = articleList.iterator()
        while (iterator.hasNext()) {
            val savedArticle = iterator.next()
            if (savedArticle.title == article.title) {
                iterator.remove()
                break
            }
        }

        val newArticleListJson = gson.toJson(articleList)
        sharedPreferences.edit().putString("articleList", newArticleListJson).apply()

        // Remove the article from the adapter's list and notify the adapter
        val position = adapter.listArr.indexOf(article)
        if (position != -1) {
            adapter.removeItem(position)
            adapter.unmarkAsFavorite(article)
        }

        // Update visibility of views based on data availability
        if (articleList.isEmpty()) {
            savedArticlesRecyclerView.visibility = View.GONE
            favText.visibility = View.VISIBLE
            favImage.visibility = View.VISIBLE
            moveToHomePage.visibility = View.VISIBLE
            //adFrameLarge.visibility = View.VISIBLE
        } else {
            savedArticlesRecyclerView.visibility = View.VISIBLE
            favText.visibility = View.GONE
            favImage.visibility = View.GONE
            moveToHomePage.visibility = View.GONE
            //adFrameLarge.visibility = View.GONE
        }
    }
}
