package com.example.finalproject

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ArchiveAdapter(private var postList: List<ArchiveData>) :
    RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>() {

    inner class ArchiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.itemTitle)
        val imgPreview: ImageView = itemView.findViewById(R.id.itemImage)
        val txtCategory: TextView = itemView.findViewById(R.id.itemCategory)
        val txtDate: TextView = itemView.findViewById(R.id.itemDate)
        val starContainer: LinearLayout = itemView.findViewById(R.id.starContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_post, parent, false)
        return ArchiveViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {
        val post = postList[position]
        holder.txtTitle.text = post.title
        holder.txtCategory.text = post.category

        // 날짜 표시
        val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val date = Date(post.timestamp)
        holder.txtDate.text = dateFormat.format(date)

        // 별점 표시
        displayStarRating(holder.starContainer, post.rating)

        // 이미지 표시
        if (post.imageUrl.isNotEmpty()) {
            val decodedBytes = Base64.decode(post.imageUrl, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
            holder.imgPreview.setImageBitmap(bitmap)
        } else {
            holder.imgPreview.setImageResource(R.drawable.gallery) // 기본 이미지
        }
    }

    private fun displayStarRating(starContainer: LinearLayout, rating: Int) {
        starContainer.removeAllViews() // 기존 별점 제거

        for (i in 1..5) {
            val starImageView = ImageView(starContainer.context)

            // 별 이미지 설정
            if (i <= rating) {
                starImageView.setImageResource(R.drawable.star1) // 채워진 별
            } else {
                starImageView.setImageResource(R.drawable.emptystar) // 빈 별
            }

            // 별 크기 설정 (dp를 px로 변환)
            val starSize = (20 * starContainer.context.resources.displayMetrics.density).toInt()
            val layoutParams = LinearLayout.LayoutParams(starSize, starSize)
            layoutParams.setMargins(2, 0, 2, 0) // 별 사이 간격
            starImageView.layoutParams = layoutParams

            starContainer.addView(starImageView)
        }
    }

    override fun getItemCount(): Int = postList.size

    fun updateData(newList: List<ArchiveData>) {
        postList = newList
        notifyDataSetChanged()
    }
}
