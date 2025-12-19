package com.practice.astra.ui.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.practice.astra.R
import com.practice.astra.data.TicketData

class TicketViewModel : ViewModel() {

    // チケット詳細
    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails

    // 購入
    private val _purchasedTickets = MutableLiveData<Pair<List<TicketData>, List<TicketData>>>()
    val unusedTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.first }
    val expiredTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.second }

    // ブックマーク済み
    private val _bookmarkedTickets = MutableLiveData<List<TicketData>>()
    val bookmarkedTickets: LiveData<List<TicketData>> = _bookmarkedTickets

    // 組織チケット
    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    // チケットのフォーマット
    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        ticket.price?.let { "¥$it" } ?: "無料"
    }

    fun formatTicketPrice(price: Int?): String {
        return price?.let { "¥$it" } ?: "無料"
    }

    // チケット詳細
    fun loadTicketDetail(ticketId: String) {
        _selectedTicketDetails.value = TicketData(
            ticketId, "銀河鉄道の夜", R.drawable.ticket_image,
            "劇団銀河", "銀河文化会館", 650, false
        )
    }

    // 購入
    fun loadPurchasedTickets() {
        val unused = listOf(
            TicketData("001", "未使用チケット", R.drawable.ticket_image, "劇団A", "ホールA", 1000, false)
        )
        val expired = listOf(
            TicketData("002", "使用済みチケット", R.drawable.ticket_image, "劇団B", "ホールB", 0, true)
        )
        _purchasedTickets.value = Pair(unused, expired)
    }

    // ブックマーク
    fun loadBookmarkedTickets() {
        _bookmarkedTickets.value = listOf(
            TicketData("003", "ブックマーク中", R.drawable.ticket_image, "劇団C", "ホールC", 1200, true)
        )
    }

    // 組織
    fun loadOrganizationTickets() {
        _organizationTickets.value = listOf(
            TicketData("004", "未使用チケット", R.drawable.ticket_image, "劇団A", "ホールA", 1000, false)
        )
    }

    // 処理
    fun toggleBookmark(ticket: TicketData) {
    }
}