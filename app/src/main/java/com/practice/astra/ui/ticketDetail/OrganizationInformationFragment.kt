package com.practice.astra.ui.ticketDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentOrganizationInformationBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class OrganizationInformationFragment : BaseTicketListFragment() {

    private var _binding: FragmentOrganizationInformationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TicketDetailViewModel by viewModels({ requireParentFragment() })

    override fun onTicketClicked(ticket: TicketData) {
        android.util.Log.d("DEBUG", "団体情報からチケットをクリック: ${ticket.id}")

        val action = TicketDetailTabFragmentDirections
            .actionTicketDetailTabSelf(ticket.id)
        parentFragment?.parentFragment?.findNavController()?.navigate(action)
    }

    override fun onBookmarkClicked(ticket: TicketData) {
        viewModel.toggleBookmark(ticket.id)
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

        val reviewAdapter = ReviewAdapter(
            onReviewClick = { reviewData ->
                val action = TicketDetailTabFragmentDirections.actionTicketDetailTabSelf(reviewData.ticketId)
                parentFragment?.parentFragment?.findNavController()?.navigate(action)
            },
            onLikeClick = { reviewData ->
                viewModel.toggleReviewLike(reviewData)
            }
        )

        binding.recyclerViewReviews.apply {
            adapter = reviewAdapter
            layoutManager = LinearLayoutManager(requireContext())
            isNestedScrollingEnabled = false
        }

        viewModel.organizationDetail.observe(viewLifecycleOwner) { org ->
            binding.textOrganizationDescription.text = org.description
            binding.textActivitySchedule.text = org.schedule
        }

        // 口コミデータの監視と更新
        viewModel.organizationReviews.observe(viewLifecycleOwner) { reviews ->
            reviewAdapter.submitList(reviews)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}