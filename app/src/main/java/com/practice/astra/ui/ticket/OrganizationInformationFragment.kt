package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.databinding.FragmentOrganizationInformationBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class OrganizationInformationFragment : BaseTicketListFragment() {

    private var _binding: FragmentOrganizationInformationBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TicketViewModel by viewModels()
    private var adapter: RecyclerAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrganizationInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.organizationTickets.observe(viewLifecycleOwner) { tickets ->
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
        viewModel.loadOrganizationTickets()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
