package com.practice.astra.ui.timeline

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.ui.base.BaseTicketListFragment
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.databinding.FragmentTimelineBinding

class TimelineFragment : BaseTicketListFragment() {

    private var _binding: FragmentTimelineBinding? = null
    private val binding get() = _binding!!

    private val TAG = "TimelineFragment"

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
        setupRecyclerView(
            binding.recyclerView,
            listData = generateListData(),
            onItemClick = {ticketData -> commonHandleItemClick(ticketData)}
        ) // チケット表示用RecyclerViewセットアップ処理
    }

    private fun generateListData(): List<TicketData> {
        return listOf(
                TicketData("どんぐりと山猫", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
                TicketData("よだかの星", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
                TicketData("セロ弾きのゴーシュ", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true),
                TicketData("銀河鉄道の夜", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 650, false),
                TicketData("どんぐりと山猫", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
                TicketData("よだかの星", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
                TicketData("セロ弾きのゴーシュ", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 700, true),
                TicketData("銀河鉄道の夜", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 700, false)
        )
    }

}