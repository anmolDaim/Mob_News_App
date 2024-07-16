package com.mobnews.app.Activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobnews.app.Adapter.ItemsAdapter
import com.mobnews.app.DataClass.Data1
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobnews.app.ApiInterface.ApiInterface
import com.mobnews.app.R
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class VideoActivity : AppCompatActivity(), ItemsAdapter.OnFavoriteSelectedListener {
    lateinit var sugestedRecyclerView: RecyclerView
    lateinit var progressBar3: ProgressBar
    lateinit var Adpater:ItemsAdapter
    lateinit var disclaimerVideo:TextView
    lateinit var httpSource:TextView
    lateinit var shareReading: ConstraintLayout
    lateinit var fontSizeReading: ConstraintLayout
    lateinit var adView:AdView
    lateinit var authortextView:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

        // Assuming you have TextViews in your layout to display these
        val videoHeadingTextView: TextView = findViewById(R.id.videoHeadingTextView)
        val videoDateTextView: TextView = findViewById(R.id.videoDateTextView)
        val videoContentTextView: TextView = findViewById(R.id.videoContentTextView)
        val videoImageView: ImageView = findViewById(R.id.videoImageView)
        val backBtn: ImageView = findViewById(R.id.backBtn)

        sugestedRecyclerView = findViewById(R.id.suggestedRecyclerView)
        progressBar3 = findViewById(R.id.progressBar2)
        disclaimerVideo=findViewById(R.id.disclaimerVideo)
        httpSource=findViewById(R.id.httpSource)
        shareReading=findViewById(R.id.shareEntertainment)
        authortextView=findViewById(R.id.authortextView)
        // Initialize the Mobile Ads SDK
        MobileAds.initialize(this) {}
        //FontSizeHelper.init(this)

        // Load the AdView from the layout
        adView = findViewById(R.id.AdView)

//        // Set the ad unit ID (only once per activity lifecycle)
//        adView.adUnitId = "ca-app-pub-1095072040188201~9751225647"
//
//        // Set the ad size
//        adView.adSize = AdSize.LARGE_BANNER

        // Load the ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
        fontSizeReading=findViewById(R.id.fontSizeEntertainment)
        //creating font size dialog box method
        fontSizeReading.setOnClickListener(){
            val dialogView = layoutInflater.inflate(R.layout.dialog_font_size, null)
            val dialogBuilder = AlertDialog.Builder(this, R.style.CustomAlertDialogStyle)
                .setView(dialogView)
            val dialog = dialogBuilder.show()

            dialogView.findViewById<AppCompatButton>(R.id.buttonApply).setOnClickListener {
                val radioGroup = dialogView.findViewById<RadioGroup>(R.id.radioGroup)
                val selectedRadioButtonId = radioGroup.checkedRadioButtonId

                val selectedFontSize = when (selectedRadioButtonId) {
                    R.id.radioButtonSmall -> 15f // Define your small font size here
                    R.id.radioButtonMedium -> 18f // Define your medium font size here
                    R.id.radioButtonLarge -> 22f // Define your large font size here
                    else -> 20f // Default to medium if none selected
                }

                // Apply the selected font size to the specific TextViews
                videoHeadingTextView.textSize = selectedFontSize
                videoContentTextView.textSize = selectedFontSize
                disclaimerVideo.textSize = selectedFontSize
                httpSource.textSize = selectedFontSize

                dialog.dismiss()
            }
        }

        backBtn.setOnClickListener {
            super.onBackPressed()
        }
        videoImageView.setOnClickListener {
            val intent = Intent(this@VideoActivity, ExoPlayerActivity::class.java)
            startActivity(intent)
        }

        //disclaimer text
        val baseText = "Disclaimer: This News Story is auto generated by computer program and has not been created or edited by Mob News."
        val spannable = SpannableString(baseText)
        // Change the color of "Name:"
        val endIndex = baseText.indexOf(":") + 1 // Assuming you want to color "Name:" only
//        spannable.setSpan(
//            ForegroundColorSpan(Color.BLACK),
//            0,
//            endIndex,
//            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )

        disclaimerVideo.setText(spannable)

        // Retrieve the data passed through the intent
        val videoHeading = intent.getStringExtra("VIDEO_HEADING")
        val videoDate = intent.getStringExtra("VIDEO_DATE")
        // Extract the date portion up to 'T' letter from the publishedAt string
        val indexOfT = videoDate?.indexOf('T')
        val dateSubstring = if (indexOfT != -1) {
            if (indexOfT != null) {
                videoDate?.substring(0, indexOfT)
            } else {
                Log.d("dateString","$indexOfT")
            }
        } else {
            videoDate
        }
        val videoAuthor=intent.getStringExtra("AUTHOR")
        val videoContent = intent.getStringExtra("VIDEO_CONTENT")
        val videoImageUrl = intent.getStringExtra("VIDEO_IMAGE_URL")
        val sourceUrl = intent.getStringExtra("VIDEO_URL")

        // Set the retrieved data to the TextViews
        authortextView.text=videoAuthor
        videoHeadingTextView.text = videoHeading
        videoDateTextView.text = dateSubstring.toString()
        videoContentTextView.text = videoContent
        //https_link.text = sourceUrl
        // Use Picasso to load the image from the URL into the ImageView
        if (!videoImageUrl.isNullOrEmpty()) {
            Picasso.get().load(videoImageUrl).into(videoImageView)
        }

        val newsSourceString = "News Source: "
        val fullNewsSourceString = "$newsSourceString$sourceUrl"
        val spannedNewsSourceString = SpannableString(fullNewsSourceString)
        val greenColor = ContextCompat.getColor(this, R.color.green)
        val blackColor = ContextCompat.getColor(this, R.color.skyBlue)
        spannedNewsSourceString.setSpan(
            ForegroundColorSpan(greenColor),
            0,
            newsSourceString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannedNewsSourceString.setSpan(
            ForegroundColorSpan(blackColor),
            newsSourceString.length,
            fullNewsSourceString.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        httpSource.text= spannedNewsSourceString

        httpSource.setOnClickListener {
            // Get the URL from the TextView
            val fullUrl = httpSource.text.toString()

            // Check if the URL is not empty
            if (fullUrl.isNotEmpty()) {
                // Extract the URL after the colon
                val url = fullUrl.substringAfter(": ")

                openChromeTab(url)
//
//                // Create an Intent to view the URL
//                val intent = Intent(this@VideoActivity, WebViewActivity::class.java).apply {
//                    putExtra("url", url)
//                }
//                // Start WebViewActivity
//                startActivity(intent)
            } else {
                // Display a toast message if the URL is empty
                httpSource.text = "No url"
            }

        }
        shareReading.setOnClickListener {
            // Get the URL from the TextView
            val newsSourceLink = httpSource.text.toString()
            val url = newsSourceLink.substringAfter(": ")

            // Create the intent
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, url)
                type = "text/plain"
            }

            // Start the activity to show the share options
            val chooser = Intent.createChooser(shareIntent, "Share via")
            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(chooser)
            } else {
                // If no apps are available to handle the intent, show a toast
                Toast.makeText(this, "No apps available to handle this action", Toast.LENGTH_SHORT).show()
            }
        }

        progressBar3.visibility = View.VISIBLE
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns =
            Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
                GsonConverterFactory.create()
            ).build()

        val call = retroIns.create(ApiInterface::class.java)
        call.getApiData("us", "46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object : Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(
                            this@VideoActivity,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                        sugestedRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@VideoActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(
                                    this@VideoActivity,
                                    ReadingActivity::class.java
                                ).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                }
                                startActivity(intent)
                            },
                            this@VideoActivity
                        )
                        sugestedRecyclerView.adapter = catAdapter
                        progressBar3.visibility = View.GONE
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase("TopHeadline")
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }
    private fun fetchArticlesFromFirebase(categoryCode: String) {
        progressBar3.visibility = View.VISIBLE
        val database = FirebaseDatabase.getInstance()
        val articlesRef = database.getReference("articles").child(categoryCode)

        articlesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val articlesList = mutableListOf<Data1.Article>()

                for (articleSnapshot in dataSnapshot.children) {
                    val author = articleSnapshot.child("author").value as? String ?: ""
                    val content = articleSnapshot.child("content").value as? String ?: ""
                    val description = articleSnapshot.child("description").value as? String ?: ""
                    val publishedAt = articleSnapshot.child("publishedAt").value as? String ?: ""
                    val title = articleSnapshot.child("title").value as? String ?: ""
                    val url = articleSnapshot.child("url").value as? String ?: ""
                    val urlToImage = articleSnapshot.child("urlToImage").value as? String ?: ""

                    // Handle nullable Source
                    // Check if essential fields are not empty
                    if (author.isNotEmpty() && title.isNotEmpty() && urlToImage.isNotEmpty()) {
                        // Handle nullable Source
                        val sourceSnapshot = articleSnapshot.child("source")
                        val source = if (sourceSnapshot.exists()) {
                            val sourceId = sourceSnapshot.child("id").value as? String ?: ""
                            val sourceName = sourceSnapshot.child("name").value as? String ?: ""
                            Data1.Article.Source(sourceId, sourceName)
                        } else {
                            null
                        }

                        val article = Data1.Article(
                            author,
                            content,
                            description,
                            publishedAt,
                            source,
                            title,
                            url,
                            urlToImage
                        )
                        articlesList.add(article)
                    }
                }
                articlesList.shuffle()
                // Update RecyclerView with the new data
                val linearLayout = LinearLayoutManager(this@VideoActivity, LinearLayoutManager.VERTICAL, false)
                sugestedRecyclerView.layoutManager = linearLayout
                val catAdapter = ItemsAdapter(this@VideoActivity, articlesList, { article ->
                    // Handle item click here for reading
                    val intent = Intent(this@VideoActivity, ReadingActivity::class.java).apply {
                        putExtra("author", article.author)
                        putExtra("content", article.content)
                        putExtra("description", article.description)
                        putExtra("publishedAt", article.publishedAt)
                        putExtra("title", article.title)
                        putExtra("urlToImage", article.urlToImage)
                        putExtra("urlToChrome", article.url)
                        putExtra("defaultImageResId", R.drawable.no_image_placeholder)
                        putExtra("categoryCode", categoryCode)
                        putExtra("isFavorite", isArticleFavorite(article))
                    }
                    startActivity(intent)
                }, this@VideoActivity)

                sugestedRecyclerView.adapter = catAdapter
                progressBar3.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }
    override fun onFavoriteSelected(article: Data1.Article) {
        val gson = Gson()
        val sharedPreferences = this.getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing article list from SharedPreferences
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val existingArticleList: MutableList<Data1.Article> = gson.fromJson(articleListJson, type) ?: mutableListOf()

        // Check if the article already exists in the list
        val existingArticle = existingArticleList.find { it.title == article.title }

        if (existingArticle != null) {
            // Remove the article from the existing list
            existingArticleList.remove(existingArticle)

            // Convert the updated list to JSON string
            val updatedArticleListJson = gson.toJson(existingArticleList)

            // Store the updated JSON string in SharedPreferences
            editor.putString("articleList", updatedArticleListJson)
            editor.apply()

            Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show()

            // Notify the adapter about the item removal
            (sugestedRecyclerView.adapter as ItemsAdapter).unmarkAsFavorite(article)
        } else {
            // Add the new article to the existing list
            existingArticleList.add(article)

            // Convert the updated list to JSON string
            val updatedArticleListJson = gson.toJson(existingArticleList)

            // Store the updated JSON string in SharedPreferences
            editor.putString("articleList", updatedArticleListJson)
            editor.apply()

            Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show()

            // Notify the adapter about the item change
            (sugestedRecyclerView.adapter as ItemsAdapter).markAsFavorite(article)
        }
    }

    private fun isArticleFavorite(article: Data1.Article): Boolean {
        val sharedPreferences = this.getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val existingArticleList: MutableList<Data1.Article> = Gson().fromJson(articleListJson, type) ?: mutableListOf()
        return existingArticleList.any { it.title == article.title }
    }


    // Inside your activity class
    fun openChromeTab(url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

}