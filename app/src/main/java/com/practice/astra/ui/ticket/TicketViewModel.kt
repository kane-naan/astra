package com.practice.astra.ui.ticket

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.practice.astra.data.TicketData
import kotlinx.coroutines.launch
import android.util.Log
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TicketViewModel : ViewModel() {

    private val _purchasedTickets = MutableLiveData<Pair<List<TicketData>, List<TicketData>>>()
    val unusedTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.first }
    val expiredTickets: LiveData<List<TicketData>> = _purchasedTickets.map { it.second }

    private val _bookmarkedTickets = MutableLiveData<List<TicketData>>()
    val bookmarkedTickets: LiveData<List<TicketData>> = _bookmarkedTickets

    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = "test_user_001" // 認証実装前なので固定


    fun formatTicketPrice(price: Int?): String {
        return price?.let { "¥$it" } ?: "無料"
    }

    fun loadPurchasedTickets() {
//        val unused = listOf(
//            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
//        )
//        val expired = listOf(
//            TicketData("001", "どんぐりと山猫", "", "やまねこ高等学校演劇部", "やまねこ高校体育館", 0, listOf("高校生"),false),
//        )
//        _purchasedTickets.value = Pair(unused, expired)
    }

    fun loadBookmarkedTickets() {
        viewModelScope.launch {
            try {
                val bookmarkSnapshot = db.collection("users").document(currentUserId)
                    .collection("bookmarks").get().await()

                val ticketIds = bookmarkSnapshot.documents.map { it.id }

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
                val bookmarkDocRef = db.collection("users").document(currentUserId)
                    .collection("bookmarks").document(ticketId)

                val doc = bookmarkDocRef.get().await()
                if (doc.exists()) {
                    bookmarkDocRef.delete().await()
                    Log.d("DEBUG_ASTRA", "削除成功")
                } else {
                    bookmarkDocRef.set(mapOf("bookmarkedAt" to com.google.firebase.Timestamp.now())).await()
                    Log.d("DEBUG_ASTRA", "保存成功！")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_ASTRA", "エラー: ${e.message}")
            }
        }
    }
}