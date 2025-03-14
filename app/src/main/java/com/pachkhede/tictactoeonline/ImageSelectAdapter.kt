package com.pachkhede.tictactoeonline

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ImageSelectAdapter(private val context : Context, val list : List<Int>) : RecyclerView.Adapter<ImageSelectAdapter.ImageSelectViewHolder>(){
    var selectedItem: Int = 0
    val selectedImage get() = list[selectedItem]
    class ImageSelectViewHolder(itemView: View) : ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById<ImageView>(R.id.imageSelectOption)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSelectViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.item_image_select, parent, false)
        return ImageSelectViewHolder(view)
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ImageSelectViewHolder, position: Int) {
        holder.imageView.setImageResource(list[position])

        if (selectedItem == position){
            holder.imageView.setBackgroundColor("#efefef".toColorInt())
        } else {
            holder.imageView.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.imageView.setOnClickListener {
            if (selectedItem != position){
                val previousSelected = selectedItem
                selectedItem = position

                notifyItemChanged(previousSelected)
                notifyItemChanged(selectedItem)
            }
        }



    }
}