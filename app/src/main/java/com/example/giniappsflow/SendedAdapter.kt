package com.example.giniappsflow

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.giniappsflow.databinding.LinearViewItemBinding
import com.example.giniappsflow.model.Image
import java.io.File

class SendedAdapter(private val onClick: (View, String) -> Unit) :
    RecyclerView.Adapter<SendedAdapter.ViewHolder>() {

    private val list: ArrayList<Image> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val path = list[position]

        holder.binding.tvLink.text = path.link

        onClick(holder.itemView,path.link)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(newItems: List<Image>) {
        for(p in newItems){
            if (File(p.uri).exists())
                list.add(p)
        }
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: LinearViewItemBinding) : RecyclerView.ViewHolder(binding.root) {
        companion object {

            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LinearViewItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}