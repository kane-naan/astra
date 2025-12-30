package com.practice.astra.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentUserBinding
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.ui.ticket.RecyclerAdapter
import com.practice.astra.ui.timeline.TimelineFragmentDirections

const val ARG_INITIAL_TAB = "initial_tab_index"
const val TAB_INDEX_FOLLOWING = 0
const val TAB_INDEX_FOLLOWER = 1

class UserFragment : BaseTicketListFragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels()
    override fun onTicketClicked(ticket: TicketData) {
        val action = UserFragmentDirections.actionUserToTicketDetailTab(ticket.id)
        findNavController().navigate(action)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        setupClickListeners()
        viewModel.loadUserData()
    }

    // 監視
    private fun setupObservers() {
        observeAndSync(viewModel.recommendedTickets, binding.recyclerViewRecommended)
        observeAndSync(viewModel.reviews, binding.recyclerViewWriting)
    }

    // フォロー・フォロワー一覧への遷移
    private fun setupClickListeners() {
        binding.textFollowing.setOnClickListener {
            navigateToFollowList(TAB_INDEX_FOLLOWING)
        }

        binding.textFollower.setOnClickListener {
            navigateToFollowList(TAB_INDEX_FOLLOWER)
        }
    }

    private fun navigateToFollowList(initialTab: Int) {
        val bundle = Bundle().apply {
            putInt(ARG_INITIAL_TAB, initialTab)
        }
        findNavController().navigate(R.id.followListFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}