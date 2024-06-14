package com.mobnews.app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.DataClass.notificationDataClass
import com.mobnews.app.R

class notificationAdapter(val notiArr:List<notificationDataClass>):RecyclerView.Adapter<notificationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
val notiHeading:(TextView)=itemView.findViewById(R.id.notiHeading)
val notiTitle:(TextView)=itemView.findViewById(R.id.notiTitle)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.notification_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notiArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.notiTitle.setText(notiArr[position].title)
        holder.notiHeading.setText(notiArr[position].heading)
    }
}