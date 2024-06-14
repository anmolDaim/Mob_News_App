package com.mobnews.app.Fragment

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobnews.app.Adapter.ImageAdapter
import com.mobnews.app.Adapter.ItemsAdapter
import com.mobnews.app.Adapter.categoryAdapter
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.DataClass.categoryDataClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mobnews.app.Activity.NotificationActivity
import com.mobnews.app.Activity.ReadingActivity
import com.mobnews.app.Activity.SearchActivity
import com.mobnews.app.ApiInterface.ApiInterface
import com.mobnews.app.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs


class HomeFragment : Fragment(), ItemsAdapter.OnFavoriteSelectedListener {
    private lateinit var cont: FragmentActivity
    private lateinit var viewPager2: ViewPager2
    private lateinit var handler: Handler
    private lateinit var imageList: ArrayList<Int>
    private lateinit var notificationDot:View
    lateinit var newsRecyclerView:RecyclerView
    lateinit var CategoryAdapter:categoryAdapter
    lateinit var progressBar:ProgressBar
    //lateinit var favouriteNews:ImageView
    lateinit var categoryRecyclerView:RecyclerView
    private lateinit var article: Data1.Article
    private val gson = Gson()
    lateinit var adView: AdView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var notiConstraintLayout:ConstraintLayout
    lateinit var searchConstraintLayout:ConstraintLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is FragmentActivity){
            cont=context
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        newsRecyclerView = view.findViewById(R.id.newsRecyclerView)
        //favouriteNews = view.findViewById(R.id.favouriteNews)
        categoryRecyclerView = view.findViewById(R.id.categoryRecyclerView)
        progressBar=view.findViewById(R.id.progressBar)
        notiConstraintLayout=view.findViewById(R.id.notiConstraintLayout)
        searchConstraintLayout=view.findViewById(R.id.searchConstraintLayout)
        notificationDot=view.findViewById(R.id.notificationDot)
        sharedPreferences = requireContext().getSharedPreferences("notifications", Context.MODE_PRIVATE)
        MobileAds.initialize(requireContext()) {}
        adView = view.findViewById(R.id.adView)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)


        notiConstraintLayout.setOnClickListener {
            val intent=Intent(cont, NotificationActivity::class.java)
            startActivity(intent)

        }
        searchConstraintLayout.setOnClickListener {
            val intent=Intent(cont, SearchActivity::class.java)
            startActivity(intent)
        }


        fetchTopHeadlines("TopHeadline")

        var catArr=ArrayList<categoryDataClass>()
        catArr.add(categoryDataClass("Hot News"))
        catArr.add(categoryDataClass("Business"))
        catArr.add(categoryDataClass("Entertainment"))
        catArr.add(categoryDataClass("General"))
        catArr.add(categoryDataClass("Health"))
        catArr.add(categoryDataClass("Science"))
        catArr.add(categoryDataClass("Sports"))
        catArr.add(categoryDataClass("Technology"))
        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.layoutManager = linearLayout
        CategoryAdapter = categoryAdapter(cont,catArr){categoryName->
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
        categoryRecyclerView.adapter = CategoryAdapter

        viewPager2 = view?.findViewById(R.id.viewPager2) ?: ViewPager2(requireContext())

        init()
        setTransformer()


    }



    private fun showDialog(message: String) {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.apply {
            setMessage(message)
            setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }


    private fun fetchTechnologyyNews( technology:String){

        val categoryCode: String=technology

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","technology","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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
    private fun fetchSportsNews( sports:String){

        val categoryCode: String=sports

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","sports","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

        val categoryCode: String=science

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","science","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

        val categoryCode: String=health

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","health","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

        val categoryCode: String=general

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","general","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

        val categoryCode: String=entertainment
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","entertainment","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

         val categoryCode: String=business
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()

        val call=retroIns.create(ApiInterface::class.java)
        call.categoryApiData("us","business","46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object :Callback<Data1>{
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            },
                            this@HomeFragment
                        )
                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
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

    private fun fetchTopHeadlines(headline:String) {

        Log.d("topHeading","entered into top heading method")
        val categoryCode=headline
        progressBar.visibility = View.VISIBLE
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
                    Log.d("topHeading","entered into top heading api response")
                    val responseBody = response.body()
                    responseBody?.let {
                        val productList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.shuffled().toMutableList()
                        val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                        newsRecyclerView.layoutManager = linearLayout
                        val catAdapter = ItemsAdapter(
                            cont,
                            productList,
                            { article ->
                                // Handle item click here for reading
                                val intent = Intent(context, ReadingActivity::class.java).apply {
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
                            this@HomeFragment
                        )

                        newsRecyclerView.adapter = catAdapter
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
                                    Log.d("topHeading", "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e("topHeading", "Error storing data in Firebase: $e")
                                }
                            Log.e("topHeading", "entering t=into fetch data method")

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

    private fun setTransformer() {
        val transformer= CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r=1-abs(position)
            page.scaleY=0.85f + r * 0.14f
        }

        viewPager2.setPageTransformer(transformer)

        viewPager2.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable,2000)
            }
        })
    }

    private fun init(){
        viewPager2 = view?.findViewById(R.id.viewPager2) as ViewPager2
        handler = android.os.Handler(Looper.myLooper()!!)

        progressBar.visibility = View.VISIBLE
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        val retroIns=Retrofit.Builder().baseUrl("https://newsapi.org/").client(client).addConverterFactory(GsonConverterFactory.create()).build()


        // Your Retrofit call to fetch API data
        val call = retroIns.create(ApiInterface::class.java)
        call.getApiData("us", "46bbdd49ab6148fbb7c6091ef59e42d2").enqueue(object : Callback<Data1> {
            override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let {
                        val articleList = it.articles.filter { article ->
                            !article.author.isNullOrEmpty() &&
                                    !article.title.isNullOrEmpty() &&
                                    !article.urlToImage.isNullOrEmpty()
                        }.toMutableList()

                        // Initialize the adapter with the list of articles and set it to ViewPager2
                        val adapter = ImageAdapter(ArrayList(articleList),viewPager2) { article ->
                            // Handle item click here
                            val intent = Intent(context, ReadingActivity::class.java)
                            intent.putExtra("author", article.author)
                            intent.putExtra("content", article.content)
                            intent.putExtra("description", article.description)
                            intent.putExtra("publishedAt", article.publishedAt)
                            intent.putExtra("title", article.title)
                            intent.putExtra("urlToImage", article.urlToImage)
                            intent.putExtra("urlToChrome", article.url)
                            intent.putExtra("defaultImageResId", R.drawable.no_image_placeholder)
                            startActivity(intent)
                        }
                        viewPager2.adapter = adapter

                        val articlesList = responseBody.articles.map { article ->
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

                        Log.d("articleList","$articleList")
                        articlesList?.let { articles ->
                            val database = FirebaseDatabase.getInstance()
                            val articlesRef = database.getReference("articles").child("viewpager")
                            articlesRef.setValue(articles)
                                .addOnSuccessListener {
                                    Log.d(TAG, "Data stored in Firebase successfully")
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error storing data in Firebase: $e")
                                }

                        }

                        progressBar.visibility = View.GONE
                    } ?: Log.e("HomeFragment", "Response body is null")
                } else {
                    Log.e("HomeFragment", "Response failed: ${response.code()}")
                    fetchArticlesFromFirebaseViewpager("viewpager")
                }
            }

            override fun onFailure(call: Call<Data1>, t: Throwable) {
                Log.e("HomeFragment", "API call failed: ${t.message}")
                progressBar.visibility = View.GONE
            }
        })

        viewPager2.offscreenPageLimit=3
        viewPager2.clipToPadding=false
        viewPager2.clipChildren=false
        viewPager2.getChildAt(0).overScrollMode= RecyclerView.OVER_SCROLL_NEVER

    }

    private fun fetchArticlesFromFirebaseViewpager(s: String) {
        val categoryCode:String=s
        Log.d("fetchArticlesFromFirebaseViewpager", "entering into the fetching data from firebase method")

        progressBar.visibility = View.VISIBLE
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


                    Log.d("FirebaseData", "Author: ${articleSnapshot.child("author").value}")
                    Log.d("FirebaseData", "Title: ${articleSnapshot.child("title").value}")
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
                val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                newsRecyclerView.layoutManager = linearLayout
                val adapter = ImageAdapter(ArrayList(articlesList),viewPager2) { article ->
                    // Handle item click here
                    val intent = Intent(context, ReadingActivity::class.java)
                    intent.putExtra("author", article.author)
                    intent.putExtra("content", article.content)
                    intent.putExtra("description", article.description)
                    intent.putExtra("publishedAt", article.publishedAt)
                    intent.putExtra("title", article.title)
                    intent.putExtra("urlToImage", article.urlToImage)
                    intent.putExtra("urlToChrome", article.url)
                    intent.putExtra("defaultImageResId", R.drawable.no_image_placeholder)
                    startActivity(intent)
                }
                viewPager2.adapter = adapter
                progressBar.visibility = View.GONE
                Log.d("FirebaseData", "RecyclerView updated with Firebase data")

            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }

    override fun onFavoriteSelected(article: Data1.Article) {
        val gson = Gson()
        val sharedPreferences = requireActivity().getSharedPreferences("article_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve existing article list from SharedPreferences
        val articleListJson = sharedPreferences.getString("articleList", null)
        val type = object : TypeToken<MutableList<Data1.Article>>() {}.type
        val existingArticleList: MutableList<Data1.Article> = gson.fromJson(articleListJson, type) ?: mutableListOf()

        // Check if the article already exists in the list
        if (existingArticleList.any { it.title == article.title }) {
            Toast.makeText(context, "You already added this item", Toast.LENGTH_SHORT).show()
            return
        }

        // Add the new article to the existing list
        existingArticleList.add(article)

        // Convert the updated list to JSON string
        val updatedArticleListJson = gson.toJson(existingArticleList)

        // Store the updated JSON string in SharedPreferences
        editor.putString("articleList", updatedArticleListJson)
        editor.apply()

        Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT).show()

//        // Navigate to the SavedFragment
//        parentFragmentManager.commit {
//            replace(R.id.fragment_container, SavedFragment())
//            addToBackStack(null)
//        }
    }

    private fun fetchArticlesFromFirebase(categoryCode: String) {

        Log.d("fetchArticlesFromFirebase", "entering into the fetching data from firebase method")

        progressBar.visibility = View.VISIBLE
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


                    Log.d("FirebaseData", "Author: ${articleSnapshot.child("author").value}")
                    Log.d("FirebaseData", "Title: ${articleSnapshot.child("title").value}")
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

                // Update RecyclerView with the new data
                val linearLayout = LinearLayoutManager(cont, LinearLayoutManager.VERTICAL, false)
                newsRecyclerView.layoutManager = linearLayout
                val catAdapter = ItemsAdapter(cont, articlesList, { article ->
                    // Handle item click here for reading
                    val intent = Intent(context, ReadingActivity::class.java).apply {
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
                }, this@HomeFragment)
                newsRecyclerView.adapter = catAdapter
                Log.d("FirebaseData", "RecyclerView updated with Firebase data")
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FirebaseError", "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable,2000)


    }
    private val runnable= Runnable {
        viewPager2.currentItem=viewPager2.currentItem+1
    }
}