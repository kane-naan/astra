package com.practice.astra.ui.timeline

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.map
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.practice.astra.data.ReviewData
import com.practice.astra.data.TicketData
import com.practice.astra.data.TimelineItem
import com.practice.astra.repository.TicketRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class TimelineViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val repository = TicketRepository()

    private val _timelineItems = MutableLiveData<List<TimelineItem>>()
    val timelineItems: LiveData<List<TimelineItem>> = _timelineItems

    val reviewItems: LiveData<List<TimelineItem.Review>> = _timelineItems.map { items ->
        items.filterIsInstance<TimelineItem.Review>()
    }

    val ticketItems: LiveData<List<TimelineItem.Ticket>> = _timelineItems.map { items ->
        items.filterIsInstance<TimelineItem.Ticket>()
    }

    fun loadTimeline() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                Log.d("TimelineViewModel", "--- 取得処理開始 ---")

                // チケットデータの取得
                val ticketSnapshot = db.collection("tickets")
                    //.orderBy("timestamp", Query.Direction.DESCENDING)
                    .get().await()

                Log.d("TimelineViewModel", "チケットSnapshot件数: ${ticketSnapshot.size()}")

                // ユーザーのブックマーク情報を取得
                val userDoc = db.collection("users").document(uid).get().await()
                val bookmarkedIds = userDoc.get("bookmark") as? List<String> ?: emptyList()

                // TicketData オブジェクトに変換
                val tickets = ticketSnapshot.documents.mapNotNull { doc ->
                    val ticket = doc.toObject(TicketData::class.java)
                    if (ticket == null) {
                        Log.e("TimelineViewModel", "Ticketのマッピングに失敗: ID=${doc.id}")
                    }
                    ticket?.copy(
                        id = doc.id,
                        isToggled = bookmarkedIds.contains(doc.id)
                    )
                }
                Log.d("TimelineViewModel", "マッピング後のチケット数: ${tickets.size}")

                // 口コミデータの取得
                val reviewSnapshot = db.collection("reviews")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get().await()

                Log.d("TimelineViewModel", "口コミSnapshot件数: ${reviewSnapshot.size()}")

                // ReviewData オブジェクトに変換
                val reviews = reviewSnapshot.documents.mapNotNull { doc ->
                    val review = doc.toObject(ReviewData::class.java)
                    if (review == null) {
                        Log.e("TimelineViewModel", "Reviewのマッピングに失敗: ID=${doc.id}")
                    }
                    review?.copy(id = doc.id)
                }
                Log.d("TimelineViewModel", "マッピング後の口コミ数: ${reviews.size}")

                // データを統合してソート
                val combinedList = (tickets.map { TimelineItem.Ticket(it) } +
                        reviews.map { TimelineItem.Review(it) })
                    .sortedByDescending { it.getSortingDate() ?: Date(0) }

                Log.d("TimelineViewModel", "最終統合データ数: ${combinedList.size}")

                // LiveDataに通知
                _timelineItems.value = combinedList // メインスレッドでの実行なのでvalueでOK

            } catch (e: Exception) {
                Log.e("TimelineViewModel", "致命的な読み込みエラー", e)
            }
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                loadTimeline()
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "ブックマーク切り替え失敗", e)
            }
        }
    }
}