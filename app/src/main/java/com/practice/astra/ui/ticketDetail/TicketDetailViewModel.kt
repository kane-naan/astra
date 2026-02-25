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
import com.practice.astra.repository.TicketRepository


class TicketDetailViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails
    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    private val repository = TicketRepository()

    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        if (ticket.price == 0 || ticket.price == null) "無料" else "¥${ticket.price}"
    }

    // チケット詳細
    fun loadTicketDetails(ticketId: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("tickets").document(ticketId).get().await()

                if (document.exists()) {
                    val userDoc = db.collection("users").document(currentUserId).get().await()
                    val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()

                    val ticket = document.toObject(TicketData::class.java)?.copy(
                        id = document.id,
                        isToggled = myBookmarks.contains(document.id) // ここで反映
                    )

                    ticket?.let {
                        _selectedTicketDetails.value = it
                        loadOrganizationInfo(it.organizationId)
                        loadOrganizationTickets(it.organizationId)
                        checkBookmarkStatus(it.id)
                    }
                } else {
                    Log.d("TicketDetailViewModel", "指定されたチケットが見つかりません: $ticketId")
                }
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "詳細データの取得に失敗しました", e)
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
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()

                val snapshot = db.collection("tickets")
                    .whereEqualTo("organizationId", organizationId)
                    .get()
                    .await()

                val tickets = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(
                        id = doc.id,
                        isToggled = myBookmarks.contains(doc.id)
                    )
                }
                _organizationTickets.value = tickets
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "団体チケット取得エラー", e)
            }
        }
    }

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked
//    private val currentUserId = "test_user_001"

    fun checkBookmarkStatus(ticketId: String){
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()

                _isBookmarked.postValue(myBookmarks.contains(ticketId))
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "状態確認エラー", e)
                _isBookmarked.postValue(false)
            }
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)

                val newState = !(_isBookmarked.value ?: false)
                _isBookmarked.postValue(newState)

                val orgId = _selectedTicketDetails.value?.organizationId
                if (orgId != null) loadOrganizationTickets(orgId)

            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "切り替え失敗", e)
            }
        }
    }
}