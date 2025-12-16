package com.practice.astra.ui.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.R
import com.practice.astra.data.TicketData
import androidx.navigation.fragment.findNavController
import com.practice.astra.databinding.FragmentUserBinding
import com.practice.astra.ui.base.BaseTicketListFragment

const val ARG_INITIAL_TAB = "initial_tab_index"
const val TAB_INDEX_FOLLOWING = 0
const val TAB_INDEX_FOLLOWER = 1

class UserFragment : BaseTicketListFragment() {

    private var _binding: FragmentUserBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(
            recyclerView = binding.recyclerViewRecommended,
            listData = generateRecommendedListData(),
            onItemClick = {ticketData -> commonHandleItemClick(ticketData)}
        )

        setupRecyclerView(
            recyclerView = binding.recyclerViewWriting,
            listData = generateWritingListData(),
            onItemClick = {ticketData -> commonHandleItemClick(ticketData)}
        )

        binding.textFollowing.setOnClickListener{
            val bundle = Bundle().apply {
                putInt(ARG_INITIAL_TAB, TAB_INDEX_FOLLOWING)
            }
            findNavController().navigate(
                R.id.followListFragment, // 遷移先ID (nav_graph.xmlで定義されていることを前提とする)
                bundle
            )
        }

        binding.textFollower.setOnClickListener{
            val bundle = Bundle().apply {
                putInt(ARG_INITIAL_TAB, TAB_INDEX_FOLLOWER)
            }
            findNavController().navigate(
                R.id.followListFragment, // 遷移先ID
                bundle
            )
        }
    }

    // データ生成
    private fun generateRecommendedListData():List<TicketData>{
        return listOf(
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true)
        )
    }
    private fun generateWritingListData():List<TicketData>{
        return listOf(
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 650, false)
        )
    }
}