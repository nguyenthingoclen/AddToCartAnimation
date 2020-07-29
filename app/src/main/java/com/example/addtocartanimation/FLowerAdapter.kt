package com.example.addtocartanimation

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_flower.view.*

class FLowerAdapter(context: Context, flowerList: MutableList<Int>, listen :OnClickListener) : RecyclerView.Adapter<FLowerAdapter.ItemViewHolder>(){

    private val dataList = flowerList
    private val listener = listen
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view =LayoutInflater.from(parent.context).inflate(R.layout.item_flower,parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindData(dataList[position], position)
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view){
        fun bindData(image: Int, position: Int) {
            itemView.imgFlower.setImageResource(image)
            itemView.imgFlowerCopy.setImageResource(image)
            itemView.imgFlowerCopy.setOnClickListener {
                listener.onItemClickListener(itemView.imgFlowerCopy)
            }
        }
    }

    interface OnClickListener{
        fun onItemClickListener(imageView: ImageView)
    }
}