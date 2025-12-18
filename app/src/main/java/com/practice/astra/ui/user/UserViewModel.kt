package com.practice.astra.ui.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practice.astra.R
import com.practice.astra.data.TicketData

class UserViewModel: ViewModel() {
    private val _recommendedTickets = MutableLiveData<List<TicketData>>()
    val recommendedTickets: LiveData<List<TicketData>> = _recommendedTickets

    private val _reviews = MutableLiveData<List<TicketData>>()
    val reviews: LiveData<List<TicketData>> = _reviews

    fun loadUserData() {
        _recommendedTickets.value = listOf(
            TicketData("R01", "おすすめ作品A", R.drawable.ticket_image, "劇団アルファ", "シアターX", 1500, false),
            TicketData("R02", "おすすめ作品B", R.drawable.ticket_image, "劇団ベータ", "ホールY", 2000, true)
        )

        _reviews.value = listOf(
            TicketData("W01", "aaa", R.drawable.ticket_image, "自分", "未定", null, false),
            TicketData("W02", "aaa", R.drawable.ticket_image, "自分", "未定", 0, false)
        )
    }
}