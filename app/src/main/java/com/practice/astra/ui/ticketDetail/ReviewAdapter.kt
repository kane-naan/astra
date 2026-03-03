package com.practice.astra.ui.ticketDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.practice.astra.R
import com.practice.astra.data.ReviewData
import com.practice.astra.databinding.ReviewItemBinding

class ReviewAdapter(
    private val onReviewClick: (ReviewData) -> Unit,
    private val onLikeClick: (ReviewData) -> Unit
) : ListAdapter<ReviewData, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    class ReviewViewHolder(
        private val binding: ReviewItemBinding,
        private val onReviewClick: (ReviewData) -> Unit,
        private val onLikeClick: (ReviewData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ReviewData) {
            val context = binding.root.context
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            binding.userName.text = review.userName
            binding.reviewTitle.text = review.title
            binding.reviewText.text = review.comment
            binding.reviewRating.rating = review.rating
            binding.likeCount.text = review.likeCount.toString()

            // いいね状態の判定とUI反映
            val isLiked = review.likedBy.contains(currentUserId)
            if (isLiked) {
                // いいね済み：塗りつぶしハート ＆ ピンク色
                binding.likeIcon.setImageResource(R.drawable.baseline_favorite_24)
                binding.likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.likePink))
            } else {
                // 未いいね：枠線ハート ＆ グレー
                binding.likeIcon.setImageResource(R.drawable.baseline_favorite_border_24)
                binding.likeIcon.setColorFilter(ContextCompat.getColor(context, R.color.blueGray))
            }

            // いいねアイコンのクリック
            binding.likeContainer.setOnClickListener {
                onLikeClick(review)
            }

            // 口コミ全体のクリック
            binding.root.setOnClickListener {
                onReviewClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding, onReviewClick, onLikeClick)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ReviewData>() {
        override fun areItemsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean = oldItem == newItem
    }
}