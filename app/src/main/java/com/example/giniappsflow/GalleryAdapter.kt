package com.example.giniappsflow

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giniappsflow.databinding.GridViewItemBinding
import com.example.giniappsflow.model.Image
import java.io.File

class GalleryAdapter(private val onClick: (View,String) -> Unit) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>()
{
    private var list: ArrayList<Image> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }
    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = list[position]

        Glide
            .with(holder.itemView)
            .load(image.uri)
            .into(holder.binding.imageView)
        if (image.success){
            holder.binding.progressBar.visibility = View.VISIBLE
        } else {
            holder.binding.progressBar.visibility = View.GONE
        }
        holder.binding.imageView.setOnClickListener {
            holder.binding.progressBar.visibility = View.VISIBLE
            onClick(it,image.uri)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<Image>) {
        for(p in newItems){
            if (File(p.uri).exists())
                list.add(p)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(
        val binding: GridViewItemBinding,
    ) : RecyclerView.ViewHolder(binding.root) {
        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = GridViewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}

