package com.mobnews.app.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.DataClass.videoDataClass
import com.mobnews.app.R
import com.mobnews.app.Activity.VideoActivity
import com.squareup.picasso.Picasso

class videoAdapter(val videoArr:List<videoDataClass>):RecyclerView.Adapter<videoAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val videoConstraintLayout:ConstraintLayout=itemView.findViewById(R.id.videoConstraintLayout)
        val videoImage:ImageView=itemView.findViewById(R.id.videoImage)
        val videoHeading:TextView=itemView.findViewById(R.id.videoHeading)
        val videoDate:TextView=itemView.findViewById(R.id.videoDate)
        val videoAuthor:TextView=itemView.findViewById(R.id.videoAuthor)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.video_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return videoArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val Current=videoArr[position]
        holder.videoHeading.setText(Current.videoHeading)
        holder.videoAuthor.setText(Current.videoAuthor)
        // Extract the date portion up to 'T' letter from the publishedAt string
        val indexOfT = Current.videoDate?.indexOf('T')
        val dateSubstring = if (indexOfT != -1) {
            if (indexOfT != null) {
                Current.videoDate?.substring(0, indexOfT)
            } else {
                Log.d("indexOfT","$indexOfT")
            }
        } else {
            Current.videoDate
        }
        holder.videoDate.text = dateSubstring.toString()
        if (!Current.videoImage.isNullOrEmpty()) {
            Picasso.get().load(Current.videoImage).into(holder.videoImage)
        }

        holder.videoConstraintLayout.setOnClickListener {
            val intent = Intent(holder.itemView.context, VideoActivity::class.java).apply {
                putExtra("VIDEO_HEADING", Current.videoHeading)
                putExtra("VIDEO_DATE", Current.videoDate)
                putExtra("VIDEO_IMAGE_URL", Current.videoImage)
                putExtra("VIDEO_CONTENT", Current.content)
                putExtra("VIDEO_URL", Current.sourceUrl)
                putExtra("AUTHOR",Current.videoAuthor)
            }
            holder.itemView.context.startActivity(intent)
        }
    }

}