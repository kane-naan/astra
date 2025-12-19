package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.databinding.FragmentPurchasedTicketsBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class PurchasedTicketsFragment : BaseTicketListFragment() {
    private var _binding: FragmentPurchasedTicketsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketViewModel by viewModels()

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
}
