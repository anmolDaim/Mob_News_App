package com.mobnews.app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mobnews.app.DataClass.Data1
import com.mobnews.app.R
import com.squareup.picasso.Picasso

class ImageAdapter(private val imageLst:ArrayList<Data1.Article>,private val viewPager2:ViewPager2,private val onItemClick: (Data1.Article) -> Unit) :RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner   class ImageViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val imageView: ImageView =itemView.findViewById(R.id.ViewPagerImage)
        val authorViewPager:TextView=itemView.findViewById(R.id.authorViewPager)
        val contentViewPager:TextView=itemView.findViewById(R.id.contentViewPager)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(imageLst[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.image_cintainer,parent,false)
        return ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
       return imageLst.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val currentItem = imageLst[position]


        holder.authorViewPager.text = currentItem.author
        holder.contentViewPager.text = currentItem.title
        Picasso.get().load(currentItem.urlToImage).into(holder.imageView)
        if(position==imageLst.size-1){
            viewPager2.post(runnable)
        }
    }
    private val runnable= Runnable {
        imageLst.addAll(imageLst)
        notifyDataSetChanged()
    }
}