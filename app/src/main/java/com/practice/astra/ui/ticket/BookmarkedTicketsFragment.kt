package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.databinding.FragmentBookmarkedTicketsBinding

class BookmarkedTicketsFragment : BaseTicketListFragment() {

    private var _binding: FragmentBookmarkedTicketsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by viewModels()
    private var adapter: RecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkedTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAndSync(viewModel.bookmarkedTickets, binding.recyclerView)
        viewModel.loadBookmarkedTickets()
    }
}
