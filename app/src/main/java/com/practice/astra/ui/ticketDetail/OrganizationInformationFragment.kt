package com.practice.astra.ui.ticketDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentOrganizationInformationBinding
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.ui.timeline.TimelineFragmentDirections

class OrganizationInformationFragment : BaseTicketListFragment() {

    private var _binding: FragmentOrganizationInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketDetailViewModel by viewModels({ requireParentFragment() })

    override fun onTicketClicked(ticket: TicketData) {
        android.util.Log.d("DEBUG", "団体情報からチケットをクリック: ${ticket.id}")
        val bundle = Bundle().apply {
            putString("ticketId", ticket.id)
        }
        requireActivity()
            .findNavController(R.id.nav_host_fragment_activity_main)
            .navigate(R.id.ticketDetailTabFragment, bundle)
    }

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

        viewModel.organizationDetail.observe(viewLifecycleOwner) { org ->
            binding.textOrganizationDescription.text = org.description
            binding.textActivitySchedule.text = org.schedule
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
