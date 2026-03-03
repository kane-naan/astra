package com.practice.astra.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentTimelineBinding

class TimelineFragment : BaseTicketListFragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimelineViewModel by viewModels()

    private lateinit var unifiedAdapter: UnifiedListAdapter

    private val TAG = "TimelineFragment"

    override fun onTicketClicked(ticket: TicketData) {
        val action = TimelineFragmentDirections
            .actionNavigationTimelineToTicketDetailTab(ticket.id)
        findNavController().navigate(action)
    }

    override fun onBookmarkClicked(ticket: TicketData) {
        viewModel.toggleBookmark(ticket.id)
    }

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

        setupAdapter()
        setupSearchView()

        // ViewModelからタイムラインアイテムを監視
        viewModel.timelineItems.observe(viewLifecycleOwner) { items ->
            unifiedAdapter.submitList(items)
        }

        // 初回読み込み
        viewModel.loadTimeline()
    }

    private fun setupSearchView() {
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // 検索ボタンが押された時はキーボードを隠す
                binding.search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })
    }

    // アダプターの初期化とRecyclerViewへの設定
    private fun setupAdapter() {
        unifiedAdapter = UnifiedListAdapter(
            onTicketClicked = { ticket ->
                val action = TimelineFragmentDirections
                    .actionNavigationTimelineToTicketDetailTab(ticket.id)
                findNavController().navigate(action)
            },
            onBookmarkClicked = { ticket ->
                viewModel.toggleBookmark(ticket.id)
            },
            onReviewClicked = { review ->
                val action = TimelineFragmentDirections
                    .actionNavigationTimelineToTicketDetailTab(review.ticketId)
                findNavController().navigate(action)
            },
            onReviewLikeClicked = { review ->
                viewModel.toggleReviewLike(review)
            }
        )

        binding.recyclerView.adapter = unifiedAdapter
    }
}