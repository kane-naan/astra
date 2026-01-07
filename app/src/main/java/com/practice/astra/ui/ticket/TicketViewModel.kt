package com.practice.astra.ui.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.practice.astra.R
import com.practice.astra.data.TicketData

class TicketViewModel : ViewModel() {

    private val _purchasedTickets = MutableLiveData<Pair<List<TicketData>, List<TicketData>>>()
    val unusedTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.first }
    val expiredTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.second }

    private val _bookmarkedTickets = MutableLiveData<List<TicketData>>()
    val bookmarkedTickets: LiveData<List<TicketData>> = _bookmarkedTickets

    fun formatTicketPrice(price: Int?): String {
        return price?.let { "¥$it" } ?: "無料"
    }

    fun loadPurchasedTickets() {
        val unused = listOf(
            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
        )
        val expired = listOf(
            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
        )
        _purchasedTickets.value = Pair(unused, expired)
    }

    fun loadBookmarkedTickets() {
        _bookmarkedTickets.value = listOf(
            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
        )
    }

    fun toggleBookmark(ticket: TicketData) {
    }
}