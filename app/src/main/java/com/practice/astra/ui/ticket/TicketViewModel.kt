package com.practice.astra.ui.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.practice.astra.data.TicketData
import kotlinx.coroutines.launch
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.repository.TicketRepository
import kotlinx.coroutines.tasks.await

class TicketViewModel : ViewModel() {

    private val _purchasedTickets = MutableLiveData<Pair<List<TicketData>, List<TicketData>>>()
    val unusedTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.first }
    val expiredTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.second }

    private val _bookmarkedTickets = MutableLiveData<List<TicketData>>()
    val bookmarkedTickets: LiveData<List<TicketData>> = _bookmarkedTickets

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val repository = TicketRepository()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "test_user_001"

    fun formatTicketPrice(price: Int?): String {
        return price?.let { "¥$it" } ?: "無料"
    }

    fun loadPurchasedTickets() {
    }

    fun loadBookmarkedTickets() {
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val rawTicketIds = userDoc.get("bookmark") as? List<String> ?: emptyList()
                val ticketIds = rawTicketIds.filter { it.isNotBlank() }

                if (ticketIds.isEmpty()) {
                    _bookmarkedTickets.postValue(emptyList())
                    return@launch
                }

                val ticketsSnapshot = db.collection("tickets")
                    .whereIn(FieldPath.documentId(), ticketIds)
                    .get().await()

                val tickets = ticketsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(
                        id = doc.id,
                        isToggled = true
                    )
                }
                _bookmarkedTickets.postValue(tickets)

            } catch (e: Exception) {
                Log.e("TicketViewModel", "ブックマーク取得エラー", e)
                _bookmarkedTickets.postValue(emptyList())
            }
        }
    }


    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                loadBookmarkedTickets()
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Bookmark切り替えエラー: ${e.message}")
            }
        }
    }
}