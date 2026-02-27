package com.practice.astra.ui.ticket

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.TicketItemBinding

class RecyclerViewHolder(val binding: TicketItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bind(data: TicketData){
        binding.ticketImage.setImageResource(R.drawable.ticket_image)
        binding.ticketTitle.text = data.title
        binding.ticketActor.text = data.actor
        binding.ticketPlace.text = data.place
        binding.ticketPrice.text = data.price.toString()

        if (data.isToggled){
            binding.ticketBookmark.setImageResource(R.drawable.baseline_bookmark_24)
        }else{
            binding.ticketBookmark.setImageResource(R.drawable.baseline_bookmark_border_24)
        }

        binding.ticketTags.removeAllViews()
        data.tags.forEach{ tagText ->
            val chip = Chip(binding.root.context, null, R.style.CustomTicketTag).apply{
                text = tagText
                isCheckable = false
                isClickable = false

                gravity = android.view.Gravity.CENTER

                chipBackgroundColor = ContextCompat.getColorStateList(context, android.R.color.white)
                setTextColor(ContextCompat.getColor(context, R.color.blueGray))
                chipStrokeColor = ContextCompat.getColorStateList(context, R.color.blueGray)
                chipStrokeWidth = 1f * context.resources.displayMetrics.density

                setEnsureMinTouchTargetSize(false)
                chipMinHeight = 0f

                val horizontalPadding = (12 * context.resources.displayMetrics.density).toInt()
                val verticalPadding = (2 * context.resources.displayMetrics.density).toInt()
                setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)

                iconStartPadding = 0f
                iconEndPadding = 0f
                closeIconStartPadding = 0f
                closeIconEndPadding = 0f

                textStartPadding = 0f
                textEndPadding = 0f
            }
            binding.ticketTags.addView(chip)
        }
    }
}