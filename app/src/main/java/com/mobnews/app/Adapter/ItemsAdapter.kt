package com.mobnews.app.Adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.DataClass.SavedDataClass
import com.mobnews.app.R
import com.squareup.picasso.Picasso

class ItemsAdapter(
    private val context: Context,
    var listArr: MutableList<Data1.Article>,
    private val onItemClick: (Data1.Article) -> Unit,
    private val listener: OnFavoriteSelectedListener,

) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>() {

    private val favoriteArticles = mutableSetOf<String>()
   // private val deletedPositions = mutableSetOf<Int>()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        val sharedPreferences = context.getSharedPreferences("article_data", Context.MODE_PRIVATE)
        favoriteArticles.addAll(sharedPreferences.getStringSet("favoriteArticles", setOf()) ?: setOf())
    }

    private fun saveFavorites() {
        val sharedPreferences = context.getSharedPreferences("article_data", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putStringSet("favoriteArticles", favoriteArticles)
            apply()
        }
    }

    fun updateArticles(newArticles: MutableList<Data1.Article>) {
        listArr.clear() // Clear existing data
        listArr.addAll(newArticles) // Add new data
        notifyDataSetChanged() // Notify the adapter of data change
    }

    fun removeItem(position: Int) {
        listArr.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, listArr.size)
    }

    interface OnFavoriteSelectedListener {
        fun onFavoriteSelected(article: Data1.Article)
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorName: TextView = itemView.findViewById(R.id.author)
        val titleName: TextView = itemView.findViewById(R.id.Title)
        val publishedAt: TextView = itemView.findViewById(R.id.publishedAt)
        val imageToUrl: ImageView = itemView.findViewById(R.id.urlToImage)
        val favoriteNews: ConstraintLayout = itemView.findViewById(R.id.favouriteNews)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(listArr[position])
                }
            }
            favoriteNews.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteSelected(listArr[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listArr.size
    }
//    override fun onDeleteSelected(article: SavedDataClass, position: Int) {
//        // Logic to find and update the corresponding article in listArr
//        val articleToDelete = listArr.find { it.title == article.titleSaved }
//        articleToDelete?.let {
//            listener.unmarkAsFavorite(it)
//        }
//    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listArr[position]
        Log.d("categoryAdapter", "Item at position $position: $currentItem")

        holder.authorName.text = currentItem.author
        holder.titleName.text = currentItem.title
        val indexOfT = currentItem.publishedAt?.indexOf('T')
        val dateSubstring = if (indexOfT != null && indexOfT != -1) {
            currentItem.publishedAt?.substring(0, indexOfT)
        } else {
            currentItem.publishedAt
        }
        holder.publishedAt.text = dateSubstring

        if (currentItem.urlToImage.isNullOrEmpty()) {
            holder.imageToUrl.setImageResource(R.drawable.no_image_placeholder)
            holder.imageToUrl.scaleType = ImageView.ScaleType.FIT_CENTER
            val params = holder.imageToUrl.layoutParams
            params.width = context.resources.getDimensionPixelSize(R.dimen.widget_size_20)
            params.height = context.resources.getDimensionPixelSize(R.dimen.widget_size_20)
            holder.imageToUrl.layoutParams = params
        } else {
            Picasso.get().load(currentItem.urlToImage).into(holder.imageToUrl)
        }
        // Check if the article is in the favorite list and update the UI accordingly
        if (favoriteArticles.contains(currentItem.title)) {
            holder.favoriteNews.setBackgroundResource(R.drawable.saved_bg) // set your favorite background
            holder.favoriteNews.findViewById<ImageView>(R.id.favImage)
                .setImageResource(R.drawable.saved_icon) // set your favorite icon
        } else {
            holder.favoriteNews.setBackgroundResource(R.drawable.icons_bg) // set your default background
            holder.favoriteNews.findViewById<ImageView>(R.id.favImage)
                .setImageResource(R.drawable.favourite_icon) // set your default icon
        }


    }
    fun markAsFavorite(article: Data1.Article) {
        if (favoriteArticles.add(article.title!!)) {
            saveFavorites()
            notifyItemChanged(listArr.indexOf(article))
        }
    }
    fun unmarkAsFavorite(article: Data1.Article) {
        if (favoriteArticles.remove(article.title!!)) {
            saveFavorites()
            notifyItemChanged(listArr.indexOf(article))
        }
    }



}
