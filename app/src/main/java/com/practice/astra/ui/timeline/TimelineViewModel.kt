package com.practice.astra.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.data.TicketData
import com.practice.astra.repository.TicketRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth

class TimelineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _timelineTickets = MutableLiveData<List<TicketData>>()
    val timelineTickets: LiveData<List<TicketData>> = _timelineTickets
    private val repository = TicketRepository()


    fun loadTimeline() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tickets").get().await()

                val userDoc = db.collection("users").document(uid).get().await()
                val bookmarkedIds = userDoc.get("bookmark") as? List<String> ?: emptyList()

                val tickets = snapshot.documents.mapNotNull { doc ->
                    val ticket = doc.toObject(TicketData::class.java)
                    ticket?.copy(
                        id = doc.id,
                        isToggled = bookmarkedIds.contains(doc.id)
                    )
                }

                _timelineTickets.postValue(tickets)

            } catch (e: Exception) {
                Log.e("TimelineViewModel", "読み込みエラー", e)
            }
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                Log.d("TimelineViewModel", "リポジトリ経由で保存/削除に成功: $ticketId")
                loadTimeline()
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "リポジトリ呼び出し失敗", e)
            }
        }
    }
}