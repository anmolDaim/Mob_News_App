package com.mobnews.app.ApiInterface

import com.mobnews.app.DataClass.Data1
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {

    @GET("v2/top-headlines")
    fun getApiData(
        @Query("country")
        countryCode: String,
        @Query("apiKey")
        apiKey:String
    ): Call<Data1>

    @GET("v2/top-headlines")
    fun categoryApiData(
        @Query("country")
        countryCode: String,
        @Query("category")
        categoryCode:String,
        @Query("apiKey")
        apiKey:String
    ):Call<Data1>
    @GET("v2/everything")
    fun searchNews(
        @Query("q")
        query: String,
        @Query("apiKey")
        apiKey: String
    ): Call<Data1>


}