package com.practice.astra.ui.timeline

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practice.astra.R
import com.practice.astra.data.TicketData

class TimelineViewModel : ViewModel() {

    private val _timelineTickets = MutableLiveData<List<TicketData>>()
    val timelineTickets: LiveData<List<TicketData>> = _timelineTickets

    fun loadTimeline() {
        _timelineTickets.value = listOf(
            TicketData("001", "どんぐりと山猫", R.drawable.ticket_image, "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, false),
            TicketData("002", "よだかの星", R.drawable.ticket_image, "よだか高等学校演劇部", "よだかホール", 0, true),
            TicketData("003", "セロ弾きのゴーシュ", R.drawable.ticket_image, "劇団セロ弾き", "セロ弾き記念ホール", 1200, true),
            TicketData("004", "銀河鉄道の夜", R.drawable.ticket_image, "劇団銀河", "銀河文化会館", 650, false)
        )
    }
}