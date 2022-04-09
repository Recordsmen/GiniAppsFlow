package com.example.giniappsflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giniappsflow.database.local.Image
import com.example.giniappsflow.databinding.GridViewItemBinding
import java.io.File

class GalleryAdapter(private val onClick: (View,String) -> Unit) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private val list: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = list[position]

        Glide
            .with(holder.itemView)
            .load(path)
            .into(holder.binding.imageView)

        holder.binding.imageView.setOnClickListener {
            holder.binding.progressBar.visibility = View.VISIBLE
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

    class ViewHolder(val binding: GridViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridViewItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}