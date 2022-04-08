package com.example.giniappsflow

import kotlinx.android.synthetic.main.grid_view_item.view.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class GalleryAdapter(private val onClick: (View,String) -> Unit) :
    RecyclerView.Adapter<GalleryAdapter.MyViewHolder>() {

    private val list: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.grid_view_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path = list[position]

        Glide
            .with(holder.itemView)
            .load(path)
            .into(holder.image)
        holder.image.clipToOutline = true
        holder.image.setOnClickListener {
            onClick(it,path)
        }


    }

    fun update(newItems: List<String>) {
        for(p in newItems){
            if (File(p).exists())
                list.add(p)
        }
        notifyDataSetChanged()
    }

    class MyViewHolder(item: View) : RecyclerView.ViewHolder(item) {
        val image = item.imageView
    }
}