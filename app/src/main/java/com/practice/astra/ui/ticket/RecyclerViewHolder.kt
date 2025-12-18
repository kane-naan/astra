package com.practice.astra.ui.ticket

import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.TicketItemBinding

class RecyclerViewHolder(val binding: TicketItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(data: TicketData){
        binding.ticketImage.setImageResource(data.image)
        binding.ticketTitle.text = data.title
        binding.ticketActor.text = data.actor
        binding.ticketPlace.text = data.place
        binding.ticketPrice.text = data.price.toString()
        if (data.isToggled){
            binding.ticketBookmark.setImageResource(R.drawable.baseline_bookmark_24)
        }else{
            binding.ticketBookmark.setImageResource(R.drawable.baseline_bookmark_border_24)
        }
    }
}