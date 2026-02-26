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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val rawPurchasedIds = userDoc.get("purchasedTicketIds") as? List<String> ?: emptyList()

                // 空文字や空白を除外
                val purchasedIds = rawPurchasedIds.filter { it.isNotBlank() }

                if (purchasedIds.isEmpty()) {
                    _purchasedTickets.postValue(Pair(emptyList(), emptyList()))
                    return@launch
                }

                // フィルタリング後の purchasedIds を使用
                val ticketsSnapshot = db.collection("tickets")
                    .whereIn(FieldPath.documentId(), purchasedIds)
                    .get().await()

                val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()

                val allPurchasedTickets = ticketsSnapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(
                        id = doc.id,
                        isToggled = myBookmarks.contains(doc.id)
                    )
                }

                val now = Date()
                val sdf = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.JAPAN)

                val unused = mutableListOf<TicketData>()
                val expired = mutableListOf<TicketData>()

                allPurchasedTickets.forEach { ticket ->
                    try {
                        val dateString = ticket.event_date.split("～")[0].trim()
                        val eventDate = sdf.parse(dateString)

                        if (ticket.stockCount > 0 && eventDate != null && eventDate.after(now)) {
                            unused.add(ticket)
                        } else {
                            expired.add(ticket)
                        }
                    } catch (e: Exception) {
                        Log.e("TicketViewModel", "日付解析失敗: ${ticket.event_date}")
                        expired.add(ticket)
                    }
                }

                _purchasedTickets.postValue(Pair(unused, expired))

            } catch (e: Exception) {
                Log.e("TicketViewModel", "購入済みチケット取得エラー", e)
                _purchasedTickets.postValue(Pair(emptyList(), emptyList()))
            }
        }
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
                loadPurchasedTickets()
                loadBookmarkedTickets()
            } catch (e: Exception) {
                Log.e("TicketViewModel", "Bookmark切り替えエラー: ${e.message}")
            }
        }
    }
}