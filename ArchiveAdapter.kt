package com.example.finalproject

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ArchiveAdapter(private var postList: List<ArchiveData>) :
    RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>() {

    inner class ArchiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.itemTitle)
        val imgPreview: ImageView = itemView.findViewById(R.id.itemImage)
        val txtCategory: TextView = itemView.findViewById(R.id.itemCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        val post = postList[position]
        holder.txtTitle.text = post.title
        holder.txtCategory.text = post.category

        if (post.imageUrl.isNotEmpty()) {
            val decodedBytes = Base64.decode(post.imageUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imgPreview.setImageBitmap(bitmap)
        } else {
            holder.imgPreview.setImageResource(R.drawable.gallery) // 기본 이미지
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updateData(newList: List<ArchiveData>) {
        postList = newList
        notifyDataSetChanged()
    }
}
