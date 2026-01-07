package com.practice.astra.ui.ticket

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.TicketItemBinding

class RecyclerAdapter (
    private val listData: MutableList<TicketData>,
    private val onItemClick: (TicketData) -> Unit,
    private val onBookmarkClick: (TicketData) -> Unit
) : RecyclerView.Adapter<RecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val binding = TicketItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val ticket = listData[position]
        holder.bind(ticket)

        holder.itemView.setOnClickListener {
            onItemClick(ticket)
        }

        holder.binding.ticketBookmark.setOnClickListener {
            onBookmarkClick(ticket)
        }
    }

    fun updateData(newData: List<TicketData>) {
        listData.clear()
        listData.addAll(newData)
        notifyDataSetChanged()
    }
}
