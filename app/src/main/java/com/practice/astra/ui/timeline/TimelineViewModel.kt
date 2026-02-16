package com.practice.astra.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.R
import com.practice.astra.data.TicketData
import com.practice.astra.repository.TicketRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TimelineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val _timelineTickets = MutableLiveData<List<TicketData>>()
    val timelineTickets: LiveData<List<TicketData>> = _timelineTickets
    private val repository = TicketRepository()

    fun loadTimeline() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("tickets").get().await()
                val bookmarkSnapshot = db.collection("users").document("test_user_001")
                    .collection("bookmarks").get().await()
                val bookmarkedIds = bookmarkSnapshot.documents.map { it.id }

                val tickets = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(
                        id = doc.id,
                        isToggled = bookmarkedIds.contains(doc.id)
                    )
                }
                _timelineTickets.value = tickets
                Log.d("TimelineViewModel", "読み込み完了: ブックマーク済み数 ${bookmarkedIds.size}")

            } catch (e: Exception) {
                Log.e("TimelineViewModel", "Firestore読み込みエラー", e)
            }
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                Log.d("TimelineViewModel", "リポジトリ経由で保存/削除に成功: $ticketId")
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "リポジトリ呼び出し失敗", e)
            }
        }
    }


}