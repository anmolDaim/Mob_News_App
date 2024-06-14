package com.mobnews.app.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mobnews.app.DataClass.secondCatDataClass
import com.mobnews.app.R

class secondCatAdapter(val arrSecond:ArrayList<secondCatDataClass>,
    private val onClickListener: (String) -> Unit)
    :RecyclerView.Adapter<secondCatAdapter.ViewHolder>() {
   inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val backgroundColor:CardView=itemView.findViewById(R.id.background)
        val imageCAtegoryIcon:ImageView=itemView.findViewById(R.id.imageCAtegoryIcon)
        val textViewCategoryName:TextView=itemView.findViewById(R.id.textViewCategoryName)
        val secondSavedCat:ConstraintLayout=itemView.findViewById(R.id.secondSavedCat)

        init {
            // Set click listener for the ConstraintLayout
            secondSavedCat.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // Invoke the click listener passing the category name
                    onClickListener(arrSecond[position].text)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view=LayoutInflater.from(parent.context).inflate(R.layout.second_category_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrSecond.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category=arrSecond[position]
        val context = holder.itemView.context
        val drawable = ContextCompat.getDrawable(context, R.drawable.background_color)?.mutate()
        drawable?.setTint(ContextCompat.getColor(context, arrSecond[position].background))
        holder.backgroundColor.background = drawable
        holder.imageCAtegoryIcon.setImageResource(category.image)
        holder.textViewCategoryName.setText(category.text)


    }
}