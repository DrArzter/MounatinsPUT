package com.example.mountainsput

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.mounatinsput.Mountain
import com.example.mounatinsput.R

class MountainAdapter(context: Context, mountains: List<Mountain>) :
    ArrayAdapter<Mountain>(context, 0, mountains) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        }

        val currentMountain = getItem(position)

        val mountainImageView: ImageView = listItemView!!.findViewById(R.id.mountainImage)
        currentMountain?.mountainImage?.let {
            val imageId = context.resources.getIdentifier(it, "drawable", context.packageName)
            mountainImageView.setImageResource(imageId)
        }

        val mountainNameTextView: TextView = listItemView.findViewById(R.id.mountainName)
        mountainNameTextView.text = currentMountain?.name

        val mountainHeightTextView: TextView = listItemView.findViewById(R.id.length)
        mountainHeightTextView.text = "${currentMountain?.length} m"

        //val mountainTimeTextView: TextView = listItemView.findViewById(R.id.time)
        //mountainTimeTextView.text = currentMountain?.time

        return listItemView
    }
}
