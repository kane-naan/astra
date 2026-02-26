package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentPurchasedTicketsBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class PurchasedTicketsFragment : BaseTicketListFragment() {
    private var _binding: FragmentPurchasedTicketsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchasedTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.loadPurchasedTickets()
    }

    private fun setupObservers() {
        observeAndSync(viewModel.unusedTickets, binding.recyclerViewUnused)
        observeAndSync(viewModel.expiredTickets, binding.recyclerViewExpired)
    }

    override fun onTicketClicked(ticket: TicketData) {
        val action = TicketFragmentDirections
            .actionNavigationDashboardToTicketDetailTab(ticket.id)
        requireActivity().findNavController(R.id.nav_host_fragment_activity_main).navigate(action)
    }

    override fun onBookmarkClicked(ticket: TicketData) {
        viewModel.toggleBookmark(ticket.id)
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPurchasedTickets()
    }
}
