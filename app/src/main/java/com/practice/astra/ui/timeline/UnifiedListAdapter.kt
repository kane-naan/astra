package com.practice.astra.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.data.ReviewData
import com.practice.astra.data.TicketData
import com.practice.astra.data.TimelineItem
import com.practice.astra.databinding.ReviewItemBinding
import com.practice.astra.databinding.TicketItemBinding
import com.practice.astra.ui.ticket.RecyclerViewHolder
import android.util.Log
import com.practice.astra.ui.ticketDetail.ReviewAdapter
import com.practice.astra.R

class UnifiedListAdapter(
    private val onTicketClicked: (TicketData) -> Unit,
    private val onBookmarkClicked: (TicketData) -> Unit,
    private val onReviewClicked: (ReviewData) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items = listOf<TimelineItem>()

    companion object {
        private const val TYPE_TICKET = 0
        private const val TYPE_REVIEW = 1
    }

    fun submitList(newItems: List<TimelineItem>) {
        items = newItems
        notifyDataSetChanged()
        Log.d("UnifiedListAdapter", "リスト更新通知: ${items.size}件")
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is TimelineItem.Ticket -> TYPE_TICKET
            is TimelineItem.Review -> TYPE_REVIEW
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TICKET -> {
                val binding = TicketItemBinding.inflate(inflater, parent, false)
                RecyclerViewHolder(binding)
            }
            TYPE_REVIEW -> {
                val binding = ReviewItemBinding.inflate(inflater, parent, false)
                ReviewAdapter.ReviewViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is TimelineItem.Ticket -> {
                val ticketHolder = holder as RecyclerViewHolder

                // 既存のbindメソッドを呼び出す
                ticketHolder.bind(item.data)

                // アイテム全体のクリック処理
                ticketHolder.itemView.setOnClickListener { onTicketClicked(item.data) }

                // ブックマークアイコンのクリック処理を追加
                ticketHolder.itemView.findViewById<android.widget.ImageView>(R.id.ticketBookmark)
                    .setOnClickListener {
                        onBookmarkClicked(item.data)
                    }
            }
            is TimelineItem.Review -> {
                val reviewHolder = holder as ReviewAdapter.ReviewViewHolder

                // 口コミアイテムのバインドとクリック処理
                reviewHolder.bind(item.data)
                reviewHolder.itemView.setOnClickListener { onReviewClicked(item.data) }
            }
        }
        Log.d("UnifiedListAdapter", "データバインド: $position, ViewType: ${getItemViewType(position)}")
    }

    override fun getItemCount(): Int = items.size
}