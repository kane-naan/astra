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
            TicketData("001", "未使用チケット", R.drawable.ticket_image, "劇団A", "ホールA", 1000, false)
        )
        val expired = listOf(
            TicketData("002", "使用済みチケット", R.drawable.ticket_image, "劇団B", "ホールB", 0, true)
        )
        _purchasedTickets.value = Pair(unused, expired)
    }

    fun loadBookmarkedTickets() {
        _bookmarkedTickets.value = listOf(
            TicketData("003", "ブックマーク中", R.drawable.ticket_image, "劇団C", "ホールC", 1200, true)
        )
    }

    fun toggleBookmark(ticket: TicketData) {
    }
}