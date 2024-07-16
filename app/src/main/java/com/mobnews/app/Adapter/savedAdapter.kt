package com.mobnews.app.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.DataClass.SavedDataClass
import com.mobnews.app.R
import com.squareup.picasso.Picasso

class savedAdapter(val savedArr: ArrayList<SavedDataClass>,
                   private val onItemClick: (SavedDataClass) -> Unit
                   , private val listener: OnFavoriteSelectedListener) : RecyclerView.Adapter<savedAdapter.ViewHolder>() {

    interface OnFavoriteSelectedListener {
        fun onDeleteSelected(article: SavedDataClass, position: Int)
    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val AuthorName: TextView = itemView.findViewById(R.id.savedauthor)
        val TitleName: TextView = itemView.findViewById(R.id.savedTitle)
        val PublishedAt: TextView = itemView.findViewById(R.id.savedpublishedAt)
        val ImageToUrl: ImageView = itemView.findViewById(R.id.savedurlToImage)
        val deleteIcon: ConstraintLayout = itemView.findViewById(R.id.delete_icon)

        init {
            deleteIcon.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onDeleteSelected(savedArr[position], position)
                }
            }
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(savedArr[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.saved_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return savedArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = savedArr[position]
        Log.d("savedAdapter", "Item at position $position: $currentItem")

        holder.AuthorName.text = currentItem.authorSaved
        holder.TitleName.text = currentItem.titleSaved
        // Extract the date portion up to 'T' letter from the publishedAt string
        val indexOfT = currentItem.publishSaved?.indexOf('T')
        val dateSubstring = if (indexOfT != -1) {
            if (indexOfT != null) {
                currentItem.publishSaved?.substring(0, indexOfT)
            } else {
                Log.d("indexOfT","$indexOfT")
            }
        } else {
            currentItem.publishSaved
        }
        holder.PublishedAt.text = dateSubstring.toString()
        Picasso.get().load(currentItem.imageSaved).into(holder.ImageToUrl)
    }

    fun removeItem(position: Int) {
        savedArr.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, savedArr.size)
    }
    fun updateList(newList: List<SavedDataClass>) {
        savedArr.clear()
        savedArr.addAll(newList)
        notifyDataSetChanged()
    }
}
