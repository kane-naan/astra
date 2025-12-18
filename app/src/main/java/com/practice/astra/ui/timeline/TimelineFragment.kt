package com.practice.astra.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentTimelineBinding
import com.practice.astra.ui.ticket.RecyclerAdapter

class TimelineFragment : BaseTicketListFragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimelineViewModel by viewModels()
    private var adapter: RecyclerAdapter? = null

    private val TAG = "TimelineFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimelineBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.timelineTickets.observe(viewLifecycleOwner) { tickets ->
            if (adapter == null) {
                adapter = setupRecyclerView(
                    recyclerView = binding.recyclerView,
                    listData = tickets,
                    onItemClick = { ticket -> commonHandleItemClick(ticket) }
                )
            } else {
                adapter?.updateData(tickets)
            }
        }
        viewModel.loadTimeline()
    }
}