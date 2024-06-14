package com.mobnews.app.Fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobnews.app.Adapter.videoAdapter
import com.mobnews.app.ApiInterface.ApiInterface
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.DataClass.videoDataClass
import com.mobnews.app.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class VideoFragment : Fragment() {

    private lateinit var videoRecyclerView: RecyclerView
    private lateinit var progressBar7: ProgressBar

    private val retrofit by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        videoRecyclerView = view.findViewById(R.id.videoRecyclerView)
        progressBar7 = view.findViewById(R.id.progressBar7)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar7.visibility = View.VISIBLE

        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        if (!isAdded) {
            return
        }

        apiInterface.getApiData("us", "46bbdd49ab6148fbb7c6091ef59e42d2")
            .enqueue(object : Callback<Data1> {
                override fun onResponse(call: Call<Data1>, response: Response<Data1>) {
                    if (response.isSuccessful) {
                        response.body()?.let { data1 ->
                            val videoList = data1.articles.filter { article ->
                                !article.urlToImage.isNullOrEmpty() &&
                                        !article.title.isNullOrEmpty() &&
                                        !article.publishedAt.isNullOrEmpty() &&
                                        !article.url.isNullOrEmpty()
                            }.map { article ->
                                videoDataClass(
                                    videoImage = article.urlToImage ?: "",
                                    videoHeading = article.title ?: "No Title",
                                    videoDate = article.publishedAt ?: "No Date",
                                    content = article.content ?: "No Content",
                                    sourceUrl = article.url ?: "no source"
                                )
                            }

                            updateRecyclerView(videoList)
                        } ?: Log.e("VideoFragment", "Response body is null")
                    } else {
                        Log.e("VideoFragment", "Response failed: ${response.code()}")
                        fetchArticlesFromFirebase("TopHeadline")
                    }
                }

                override fun onFailure(call: Call<Data1>, t: Throwable) {
                    Log.e("VideoFragment", "API call failed: ${t.message}")
                }
            })
    }

    private fun fetchArticlesFromFirebase(categoryCode: String) {
        if (!isAdded) {
            return
        }

        val database = FirebaseDatabase.getInstance()
        val articlesRef = database.getReference("articles").child(categoryCode)

        articlesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val articlesList = ArrayList<videoDataClass>()

                for (articleSnapshot in dataSnapshot.children) {
                    val author = articleSnapshot.child("author").value as? String ?: ""
                    val content = articleSnapshot.child("content").value as? String ?: ""
                    val description = articleSnapshot.child("description").value as? String ?: ""
                    val publishedAt = articleSnapshot.child("publishedAt").value as? String ?: ""
                    val title = articleSnapshot.child("title").value as? String ?: ""
                    val url = articleSnapshot.child("url").value as? String ?: ""
                    val urlToImage = articleSnapshot.child("urlToImage").value as? String ?: ""

                    if (author.isNotEmpty() && title.isNotEmpty() && urlToImage.isNotEmpty()) {
                        val article = videoDataClass(
                            urlToImage,
                            title,
                            publishedAt,
                            content,
                            url
                        )
                        articlesList.add(article)
                    }
                }

                articlesList.shuffle()
                updateRecyclerView(articlesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("VideoFragment", "Error fetching data from Firebase: ${databaseError.message}")
            }
        })
    }

    private fun updateRecyclerView(dataList: List<videoDataClass>) {
        if (!isAdded) {
            return
        }

        val linearLayout = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        videoRecyclerView.layoutManager = linearLayout
        val adapter = videoAdapter(dataList)
        videoRecyclerView.adapter = adapter

        progressBar7.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clean up any resources if needed
    }
}
