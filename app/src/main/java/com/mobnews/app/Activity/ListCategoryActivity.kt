package com.mobnews.app.Activity

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListCategoryActivity : AppCompatActivity(), ItemsAdapter.OnFavoriteSelectedListener {
    lateinit var headingName:TextView
    lateinit var listCategoryRecyclerView:RecyclerView
    lateinit var backBtn:ImageView
    lateinit var progressBar: ProgressBar
    lateinit var adapter:ItemsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_category)
        headingName=findViewById(R.id.headingName)
        listCategoryRecyclerView=findViewById(R.id.listCategoryRecyclerView)
        progressBar=findViewById(R.id.progressBar)
        backBtn=findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        val categoryName = intent.getStringExtra("categoryName")

        headingName.text=categoryName
        when (categoryName) {
            "Hot News" -> fetchTopHeadlines("TopHeadline")
            "Business" -> fetchBusinessNews("business")
            "Entertainment" -> fetchEntertainmentNews("entertainment")
            "General" -> fetchGeneralNews("General")
            "Health"-> fetchHealthNews("Health")
            "Science" -> fetchScienceNews("Science")
            "Sports" -> fetchSportsNews("Sports")
            "Technology"->fetchTechnologyyNews("Technology")

        }
    }
    private fun fetchTopHeadlines(headlines:String) {
val categoryCode:String=headlines
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.getApiData("us","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("defaultImageResId", R.drawable.no_image_placeholder)

                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )

                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }
    private fun fetchTechnologyyNews(technology:String){

val categoryCode=technology
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","technology","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                        progressBar.visibility = View.GONE
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }
    private fun fetchSportsNews(sports:String){

        val categoryCode=sports
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","sports","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
                progressBar.visibility = View.GONE
            }


        })
    }

    private fun fetchScienceNews(science:String){

val categoryCode=science
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","science","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }

    private fun fetchHealthNews(health:String) {

        val categoryCode:String=health
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","health","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "RespoprogressBar.visibility = View.GONE")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }

    private fun fetchGeneralNews(general:String) {
        val categoryCode:String=general
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","general","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }

    private fun fetchEntertainmentNews(entertainment: String) {

        val categoryCode:String=entertainment
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","entertainment","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)
                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
            }


        })
    }

    private fun fetchBusinessNews(business: String) {

val categoryCode:String=business
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns= Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(
            GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","business","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :
            Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                        listCategoryRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            this@ListCategoryActivity,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                                    putExtra("author", article.author)
                                    putExtra("content", article.content)
                                    putExtra("description", article.description)
                                    putExtra("publishedAt", article.publishedAt)
                                    putExtra("title", article.title)
                                    putExtra("urlToImage", article.urlToImage)
                                    putExtra("urlToChrome", article.url)
                                    putExtra("categoryCode", categoryCode)

                                }
                                startActivity(intent)
                            },
                            this@ListCategoryActivity
                        )
                        listCategoryRecyclerView.adapter = catAdapter
                        val articlesList = responseBody?.articles?.map { article ->
                            mapOf(
                                "author" to article.author,
                                "content" to article.content,
                                "description" to article.description,
                                "publishedAt" to article.publishedAt,
                                "title" to article.title,
                                "url" to article.url,
                                "urlToImage" to article.urlToImage
                            )
                        }

                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child(categoryCode)
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(ContentValues.TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(ContentValues.TAG, "Error storing data in Firebase: $e")
                                }


                        }
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebase(categoryCode)
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
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
        if (existingArticleList.any { it.title == article.title }) {
            Toast.makeText(this, "You already added this item", Toast.LENGTH_SHORT).show()
            return
        }

        // Add the new article to the existing list
        existingArticleList.add(article)

        // Convert the updated list to JSON string
        val updatedArticleListJson = gson.toJson(existingArticleList)

        // Store the updated JSON string in SharedPreferences
        editor.putString("articleList", updatedArticleListJson)
        editor.apply()

        Toast.makeText(this, "Added successfully", Toast.LENGTH_SHORT).show()
    }


    private fun fetchArticlesFromFirebase(categoryCode: String) {

        progressBar.visibility=View.VISIBLE
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
                val linearLayout = LinearLayoutManager(this@ListCategoryActivity, LinearLayoutManager.VERTICAL, false)
                listCategoryRecyclerView.layoutManager = linearLayout
                val catAdapter = ItemsAdapter(this@ListCategoryActivity, articlesList, { article ->
                    // Handle item click here for reading
                    val intent = Intent(this@ListCategoryActivity, ReadingActivity::class.java).apply {
                        putExtra("author", article.author)
                        putExtra("content", article.content)
                        putExtra("description", article.description)
                        putExtra("publishedAt", article.publishedAt)
                        putExtra("title", article.title)
                        putExtra("urlToImage", article.urlToImage)
                        putExtra("urlToChrome", article.url)
                        putExtra("defaultImageResId", R.drawable.no_image_placeholder)
                        putExtra("categoryCode", categoryCode)
                    }
                    startActivity(intent)
                }, this@ListCategoryActivity)
                listCategoryRecyclerView.adapter = catAdapter
                progressBar.visibility=View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(ContentValues.TAG, "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }

}