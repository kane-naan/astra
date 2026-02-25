package com.practice.astra.ui.ticketDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.practice.astra.R
import com.practice.astra.databinding.FragmentTicketInformationBinding

class TicketInformationFragment : Fragment() {

    private var _binding: FragmentTicketInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.selectedTicketDetails.observe(viewLifecycleOwner) { ticket ->
            binding.textContentDetail.text = ticket.description
            binding.textDateDetail.text = ticket.event_date
            binding.textHighlightDetail.text = ticket.point
            binding.textLocationDetail.text = ticket.place
            binding.includeTicketItem.ticketImage.setImageResource(R.drawable.ticket_image)
            binding.includeTicketItem.ticketActor.text = ticket.actor

            binding.includeTicketItem.apply {
                ticketTitle.text = ticket.title
                ticketActor.text = ticket.actor
                ticketPlace.text = ticket.place
                ticketPrice.text = ticket.price?.toString() ?: "0"
                ticketImage.setImageResource(R.drawable.ticket_image)

                val icon = if (ticket.isToggled) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24
                ticketBookmark.setImageResource(icon)
                ticketBookmark.setOnClickListener {
                    viewModel.toggleBookmark(ticket.id)
                }
            }
        }
        viewModel.isBookmarked.observe(viewLifecycleOwner) { isBookmarked ->
            val icon = if (isBookmarked) R.drawable.baseline_bookmark_24 else R.drawable.baseline_bookmark_border_24
            binding.includeTicketItem.ticketBookmark.setImageResource(icon)
        }

        viewModel.formattedPrice.observe(viewLifecycleOwner) { priceString ->
            binding.textPriceDetail.text = priceString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}