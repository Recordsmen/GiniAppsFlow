package com.example.giniappsflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.giniappsflow.databinding.GridViewItemBinding
import com.example.giniappsflow.model.Image
import java.io.File

class GalleryAdapter(private val onClick: (View,String) -> Unit) :
    RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var list: ArrayList<Image> = ArrayList()

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

    fun update(newItems: List<Image>) {
        for(p in newItems){
            if (File(p.uri).exists())
                list.add(p)
        }
        val diffCallback = UsersDiffCallback(list,newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        list = newItems as ArrayList<Image>
        diffResult.dispatchUpdatesTo(this)
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

class UsersDiffCallback(
    private val oldList:List<Image>,
    private val newlist:List<Image>
) : DiffUtil.Callback(){
    override fun getOldListSize():Int = oldList.size

    override fun getNewListSize(): Int = newlist.size


    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newlist[newItemPosition]
        return oldImage.status == newImage.status
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldImage = oldList[oldItemPosition]
        val newImage = newlist[newItemPosition]
        return oldImage == newImage
    }

}