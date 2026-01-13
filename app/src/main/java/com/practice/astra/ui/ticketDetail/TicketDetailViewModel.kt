package com.practice.astra.ui.ticketDetail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.R
import com.practice.astra.data.OrganizationData
import com.practice.astra.data.TicketData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TicketDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails

    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        if (ticket.price == 0 || ticket.price == null) "無料" else "¥${ticket.price}"
    }

    // チケット詳細
    fun loadTicketDetails(ticketId: String) {
        viewModelScope.launch {
            try{
                val document = db.collection("tickets").document(ticketId).get().await()

                if (document.exists()){
                    val ticket = document.toObject(TicketData::class.java)?.copy(id = document.id)
                    ticket?.let {
                        _selectedTicketDetails.value = it
                        loadOrganizationInfo(it.organizationId)
                        loadOrganizationTickets(it.organizationId)
                    }
                }else{
                    Log.d("TicketDetailViewModel", "指定されたチケットが見つかりません: $ticketId")
                }

            }catch(e:Exception){
                Log.e("TicketDetailViewModel","詳細データの取得に失敗しました", e)
            }
        }
    }

    // 組織
    private val _organizationDetail = MutableLiveData<OrganizationData>()
    val organizationDetail: LiveData<OrganizationData> = _organizationDetail

    // 団体情報を取得
    fun loadOrganizationInfo(organizationId: String) {
        viewModelScope.launch {
            try {
                val doc = db.collection("organizations").document(organizationId).get().await()
                if (doc.exists()) {
                    _organizationDetail.value = doc.toObject(OrganizationData::class.java)
                }
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "団体情報取得エラー", e)
            }
        }
    }

    // 団体のチケット一覧取得
    fun loadOrganizationTickets(organizationId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tickets")
                    .whereEqualTo("organizationId", organizationId)
                    .get()
                    .await()

                val tickets = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(id = doc.id)
                }
                _organizationTickets.value = tickets
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "団体チケット取得エラー", e)
            }
        }
    }
}