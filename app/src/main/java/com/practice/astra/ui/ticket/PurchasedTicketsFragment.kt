package com.practice.astra.ui.ticket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentPurchasedTicketsBinding
import com.practice.astra.ui.base.BaseTicketListFragment

class PurchasedTicketsFragment : BaseTicketListFragment() {
    private var _binding: FragmentPurchasedTicketsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchasedTicketsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val unusedAdapter = setupRecyclerView(
            recyclerView = binding.recyclerViewUnused,
            listData = generateUnusedListData(),
            onItemClick = {ticketData -> commonHandleItemClick(ticketData)}
        )

        val expiredAdapter = setupRecyclerView(
            recyclerView = binding.recyclerViewExpired,
            listData = generateExpiredListData(),
            onItemClick = {ticketData -> commonHandleItemClick(ticketData)}
        )
    }

    // データ生成
    private fun generateUnusedListData():List<TicketData>{
        return listOf(
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 650, false),
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 700, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 700, false)
        )
    }
    private fun generateExpiredListData():List<TicketData>{
        return listOf(
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 650, false),
            TicketData("aaaaa", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("aaaaa", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 700, true),
            TicketData("aaaaa", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 700, false)
        )
    }
}
