package com.practice.astra.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentTimelineBinding
import com.practice.astra.repository.TicketRepository

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
        viewModel.filteredTimelineItems.observe(viewLifecycleOwner) { items ->
            unifiedAdapter.submitList(items)
        }
        binding.search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // キーボードの検索ボタンが押された時の処理（必要であれば）
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // 文字が入力されるたびにフィルタリングを依頼
                viewModel.setSearchQuery(newText ?: "")
                return true
            }
        })

        viewModel.loadTimeline()
    }

    // アダプターの初期化とRecyclerViewへの設定
    private fun setupAdapter() {
        unifiedAdapter = UnifiedListAdapter(
            onTicketClicked = { ticket ->
                // チケットクリック時の処理
                val action = TimelineFragmentDirections
                    .actionNavigationTimelineToTicketDetailTab(ticket.id)
                findNavController().navigate(action)
            },
            onBookmarkClicked = { ticket ->
                // ブックマーク処理
                viewModel.toggleBookmark(ticket.id)
            },
            onReviewClicked = { review ->
                // 口コミクリック時の処理
            }
        )

        // RecyclerViewに新しいアダプターを設定
        binding.recyclerView.adapter = unifiedAdapter
    }
}