package com.practice.astra.ui.ticketDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.practice.astra.databinding.FragmentTicketDetailTabBinding
import com.practice.astra.ui.base.BaseTabFragment

class TicketDetailTabFragment : BaseTabFragment() {

    private var _binding: FragmentTicketDetailTabBinding? = null
    private val binding get() = _binding!!

    private val args: TicketDetailTabFragmentArgs by navArgs()
    private val viewModel: TicketDetailViewModel by viewModels()

    override val tabTitles: List<String> = listOf("チケット情報", "団体情報")
    override val bindingRoot: View get() = binding.root
    override fun getTabLayout() = binding.ticketDetailTabLayout
    override fun getViewPager() = binding.pager

    override fun createTabFragment(position: Int): Fragment {
        return when(position){
            0 -> TicketInformationFragment()
            1 -> OrganizationInformationFragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTicketDetailTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        if (viewModel.selectedTicketDetails.value == null) {
            viewModel.loadTicketDetails(args.ticketId)
        }
//        viewModel.loadTicketDetails(args.ticketId)
//        viewModel.selectedTicketDetails.observe(viewLifecycleOwner) { ticket ->
//            viewModel.loadOrganizationTickets(ticket.actor)
//            viewModel.loadOrganizationInfo(ticket.actor)
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}