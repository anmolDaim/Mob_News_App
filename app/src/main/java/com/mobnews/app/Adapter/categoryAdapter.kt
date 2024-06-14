package com.mobnews.app.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.DataClass.categoryDataClass
import com.mobnews.app.R

class categoryAdapter(var context:Context, val catArr:ArrayList<categoryDataClass>, private val onClickListener: (String) -> Unit):RecyclerView.Adapter<categoryAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = 0
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val categoryName:TextView=itemView.findViewById(R.id.categoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return catArr.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = catArr[holder.adapterPosition]
        holder.categoryName.text = category.catName
        holder.itemView.setOnClickListener {
            val newPosition = holder.adapterPosition // Use holder.getAdapterPosition()

            // Check if the adapter position is valid
            if(newPosition != RecyclerView.NO_POSITION) {
                // Invoke the click listener passing the category name
                onClickListener(category.catName)

                val previousItem = selectedItemPosition
                selectedItemPosition = newPosition

                // Notify to refresh the clicked and previously clicked items
                notifyItemChanged(previousItem)
                notifyItemChanged(newPosition)
            }
        }


        if (position == selectedItemPosition) {
            holder.categoryName.setBackgroundColor(ContextCompat.getColor(context, R.color.green))
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.white))
        } else {
            holder.categoryName.setBackgroundColor(ContextCompat.getColor(context, R.color.cat_bg))
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.lightGrey))
        }
    }
}