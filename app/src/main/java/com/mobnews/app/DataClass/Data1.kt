package com.mobnews.app.DataClass


import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose

data class Data1(
    @SerializedName("articles")
    @Expose
    val articles: MutableList<Article>,
    @SerializedName("status")
    @Expose
    val status: String,
    @SerializedName("totalResults")
    @Expose
    val totalResults: Int
) {
    data class Article(
        @SerializedName("author")
        @Expose
        val author: String?,
        @SerializedName("content")
        @Expose
        val content: String?,
        @SerializedName("description")
        @Expose
        val description: String?,
        @SerializedName("publishedAt")
        @Expose
        val publishedAt: String?,
        @SerializedName("source")
        @Expose
        val source: Source?,
        @SerializedName("title")
        @Expose
        val title: String?,
        @SerializedName("url")
        @Expose
        val url: String?,
        @SerializedName("urlToImage")
        @Expose
        val urlToImage: String?
    ): Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readParcelable(Source::class.java.classLoader)!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(author)
            parcel.writeString(title)
            parcel.writeString(description)
            parcel.writeString(url)
            parcel.writeString(urlToImage)
            parcel.writeParcelable(source, flags)
        }
        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Article> {
            override fun createFromParcel(parcel: Parcel): Article {
                return Article(parcel)
            }

            override fun newArray(size: Int): Array<Article?> {
                return arrayOfNulls(size)
            }
        }

        data class Source(
            @SerializedName("id")
            @Expose
            val id: String,
            @SerializedName("name")
            @Expose
            val name: String
        ): Parcelable {
            constructor(parcel: Parcel) : this(
                parcel.readString()!!,
                parcel.readString()!!
            )
            override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeString(id)
                parcel.writeString(name)
            }

            override fun describeContents(): Int {
                return 0
            }

            companion object CREATOR : Parcelable.Creator<Source> {
                override fun createFromParcel(parcel: Parcel): Source {
                    return Source(parcel)
                }

                override fun newArray(size: Int): Array<Source?> {
                    return arrayOfNulls(size)
                }
            }
        }

    }
}