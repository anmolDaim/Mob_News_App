package com.mobnews.app.Fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils.replace
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.Adapter.savedAdapter
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.DataClass.SavedDataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobnews.app.Activity.ReadingActivity
import com.mobnews.app.R
import org.w3c.dom.Text


class SavedFragment : Fragment(), savedAdapter.OnFavoriteSelectedListener {
    private lateinit var savedArticlesRecyclerView: RecyclerView
    lateinit var favText:TextView
    lateinit var favImage:ImageView
    lateinit var moveToHomePage:TextView
    lateinit var Adapter:savedAdapter

    fun setData(bundle: Bundle) {
        arguments = bundle
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_saved, container, false)
        savedArticlesRecyclerView = view.findViewById(R.id.savedArticlesRecyclerView)
        favImage=view.findViewById(R.id.favImage)
        favText=view.findViewById(R.id.favText)
        moveToHomePage=view.findViewById(R.id.moveToHomePage)
        moveToHomePage.setOnClickListener{
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }
        return view
    }
    override fun onResume() {
        super.onResume()
        loadSavedArticles()
    }

    private fun loadSavedArticles() {
        val gson = Gson()
        val sharedPreferences = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)

        // Retrieve JSON string from SharedPreferences
        val articleListJson = sharedPreferences.getString("articleList", null)

        // Check if articleListJson is null or empty
        if (articleListJson.isNullOrEmpty()) {
            // Handle the case where the JSON string is null or empty
            return
        }

        // Convert JSON string back to list
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val articleList: MutableList<Data1.Article> = gson.fromJson(articleListJson, type)

        val arrSaved = ArrayList<SavedDataClass>()
        articleList.forEach { article ->
            val savedData = SavedDataClass(
                article.author ?: "",
                article.title ?: "",
                article.publishedAt ?: "",
                article.urlToImage ?: "",
                article.description?:"",
                article.content?:"",
                article.url?:""
            )
            arrSaved.add(0,savedData)
        }

        // Update RecyclerView with the new data
        Adapter.updateList(arrSaved)

        // Update visibility of views based on data availability
        if (arrSaved.isEmpty()) {
            savedArticlesRecyclerView.visibility = View.GONE
            favText.visibility = View.VISIBLE
            favImage.visibility = View.VISIBLE
            moveToHomePage.visibility=View.VISIBLE
        } else {
            savedArticlesRecyclerView.visibility = View.VISIBLE
            favText.visibility = View.GONE
            favImage.visibility = View.GONE
            moveToHomePage.visibility=View.GONE
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        val gson = Gson()
//        val sharedPreferences = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)
//
//// Retrieve JSON string from SharedPreferences
//        val articleListJson = sharedPreferences.getString("articleList", null)

        val gson = Gson()
        val sharedPreferences = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)

        // Retrieve JSON string from SharedPreferences
        val articleListJson = sharedPreferences.getString("articleList", null)

        // Check if articleListJson is null or empty
        if (articleListJson.isNullOrEmpty()) {
            // Handle the case where the JSON string is null or empty
            return
        }


// Convert JSON string back to list
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val articleList: MutableList<Data1.Article> = gson.fromJson(articleListJson, type)

        val arrSaved=ArrayList<SavedDataClass>()
       // if (author != null && title != null && publishedAt != null && urlToImage != null) {
         //   arrSaved.add(SavedDataClass(author, title, publishedAt, urlToImage))
        //}
        articleList.forEach { article ->
            val savedData = SavedDataClass(
                article.author ?: "",
                article.title ?: "",
                article.publishedAt ?: "",
                article.urlToImage ?: "",
                article.content?:"",
                article.description?:"",
                article.url?:""
            )
            arrSaved.add(savedData)
        }


        val linearLayout=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        savedArticlesRecyclerView.layoutManager = linearLayout
        Adapter = savedAdapter(arrSaved,onItemClick = { savedData ->
            val intent = Intent(requireContext(), ReadingActivity::class.java).apply {
                putExtra("author", savedData.authorSaved)
                putExtra("content", savedData.titleSaved)
                putExtra("description", savedData.description)
                putExtra("publishedAt", savedData.publishSaved)
                putExtra("title", savedData.titleSaved)
                putExtra("urlToImage", savedData.imageSaved)
                putExtra("urlToChrome", savedData.urlToChrome)
                putExtra("defaultImageResId", R.drawable.no_image_placeholder)
            }
            startActivity(intent)
        },
            listener = this
        )

        savedArticlesRecyclerView.adapter = Adapter

        if (arrSaved.isEmpty()) {
            savedArticlesRecyclerView.visibility = View.GONE
            favText.visibility = View.VISIBLE
            favImage.visibility = View.VISIBLE
        } else {
            savedArticlesRecyclerView.visibility = View.VISIBLE
            favText.visibility = View.GONE
            favImage.visibility = View.GONE
        }
    }

    override fun onDeleteSelected(article: SavedDataClass, position: Int) {
        Adapter.removeItem(position)
        removeFromSharedPreferences(article)
    }

    fun removeFromSharedPreferences(article: SavedDataClass) {
        val prefs = requireContext().getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = prefs.getString("articleList", null)
        val type = object : TypeToken<ArrayList<Data1.Article>>() {}.type
        val articleList: ArrayList<Data1.Article> = gson.fromJson(json, type) ?: ArrayList()

        // Find the index of the article to remove
        val index = articleList.indexOfFirst { it.author == article.authorSaved && it.title == article.titleSaved }

        // Remove the article from the list if found
        if (index != -1) {
            articleList.removeAt(index)
            // Save the updated list back to SharedPreferences
            prefs.edit().putString("articleList", gson.toJson(articleList)).apply()

            // Reload saved articles from SharedPreferences
            loadSavedArticles()
        }
    }
}