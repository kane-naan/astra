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
//        _recommendedTickets.value = listOf(
//            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
//        )
//
//        _reviews.value = listOf(
//            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
//        )
    }
}