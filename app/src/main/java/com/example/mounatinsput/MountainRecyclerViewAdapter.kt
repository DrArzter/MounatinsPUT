package com.example.mountainsput

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mounatinsput.Mountain
import com.example.mounatinsput.R

class MountainRecyclerViewAdapter(
    private val context: Context,
    private val mountains: List<Mountain>,
    private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MountainRecyclerViewAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(mountain: Mountain)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mountainImageView: ImageView = itemView.findViewById(R.id.mountainImage)
        val mountainNameTextView: TextView = itemView.findViewById(R.id.mountainName)

        fun bind(mountain: Mountain) {
            mountainNameTextView.text = mountain.name
            val resourceId = context.resources.getIdentifier(mountain.mountainImage, "drawable", context.packageName)
            Glide.with(context).load(resourceId).into(mountainImageView)
            itemView.setOnClickListener {
                itemClickListener.onItemClick(mountain)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_mountain, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mountains[position])
    }

    override fun getItemCount(): Int {
        return mountains.size
    }
}
