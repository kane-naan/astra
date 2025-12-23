package com.practice.astra.ui.ticketDetail

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
    private val viewModel: TicketDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrganizationInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAndSync(viewModel.organizationTickets, binding.recyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
