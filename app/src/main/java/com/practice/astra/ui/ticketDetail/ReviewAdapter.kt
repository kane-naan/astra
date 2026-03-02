package com.practice.astra.ui.ticketDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.data.ReviewData
import com.practice.astra.databinding.ReviewItemBinding

/**
 * 口コミ一覧用のアダプター
 * @param onReviewClick アイテムクリック時に呼び出されるコールバック。引数はReviewData全体。
 */
class ReviewAdapter(
    private val onReviewClick: (ReviewData) -> Unit
) : ListAdapter<ReviewData, ReviewAdapter.ReviewViewHolder>(DiffCallback) {

    class ReviewViewHolder(
        private val binding: ReviewItemBinding,
        private val onReviewClick: (ReviewData) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(review: ReviewData) {
            binding.userName.text = review.userName
            binding.reviewTitle.text = review.title
            binding.reviewText.text = review.comment
            binding.reviewRating.rating = review.rating
            binding.likeCount.text = review.likeCount.toString()

            binding.root.setOnClickListener {
                onReviewClick(review)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ReviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReviewViewHolder(binding, onReviewClick)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<ReviewData>() {
        override fun areItemsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ReviewData, newItem: ReviewData): Boolean {
            return oldItem == newItem
        }
    }
}