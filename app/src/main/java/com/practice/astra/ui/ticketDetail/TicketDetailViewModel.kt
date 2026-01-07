package com.practice.astra.ui.ticketDetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import com.practice.astra.R
import com.practice.astra.data.TicketData

class TicketDetailViewModel : ViewModel() {
    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails

    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        ticket.price?.let { "¥$it" } ?: "無料"
    }

    // チケット詳細
    fun loadTicketDetails(ticketId: String) {
        _selectedTicketDetails.value = TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false)
    }

    // 組織
    fun loadOrganizationTickets() {
        _organizationTickets.value = listOf(
            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
        )
    }
}