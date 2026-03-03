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

    private var allCombinedItems = listOf<TimelineItem>()

    private val _searchQuery = MutableLiveData<String>("")
    val searchQuery: LiveData<String> = _searchQuery

    val reviewItems: LiveData<List<TimelineItem.Review>> = _timelineItems.map { items ->
        items.filterIsInstance<TimelineItem.Review>()
    }

    val ticketItems: LiveData<List<TimelineItem.Ticket>> = _timelineItems.map { items ->
        items.filterIsInstance<TimelineItem.Ticket>()
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
        filterTimeline(query) // Firebase再取得ではなくローカルフィルタを実行
    }

    fun loadTimeline() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                Log.d("TimelineViewModel", "--- Firestoreからデータ全件取得開始 ---")

                val ticketSnapshot = db.collection("tickets")
                    .orderBy("event_date", Query.Direction.DESCENDING)
                    .get().await()

                val userDoc = db.collection("users").document(uid).get().await()
                val bookmarkedIds = userDoc.get("bookmark") as? List<String> ?: emptyList()

                val allTickets = ticketSnapshot.documents.mapNotNull { doc ->
                    val ticket = doc.toObject(TicketData::class.java)
                    ticket?.copy(
                        id = doc.id,
                        isToggled = bookmarkedIds.contains(doc.id)
                    )
                }

                val reviewSnapshot = db.collection("reviews")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get().await()

                val allReviews = reviewSnapshot.documents.mapNotNull { doc ->
                    val review = doc.toObject(ReviewData::class.java)
                    review?.copy(id = doc.id)
                }

                allCombinedItems = (allTickets.map { TimelineItem.Ticket(it) } +
                        allReviews.map { TimelineItem.Review(it) })
                    .sortedByDescending { it.getSortingDate() ?: Date(0) }

                // 初回読み込み時はフィルタなしで表示
                filterTimeline(_searchQuery.value ?: "")

            } catch (e: Exception) {
                Log.e("TimelineViewModel", "致命的な読み込みエラー", e)
            }
        }
    }

    private fun filterTimeline(query: String) {
        val filteredList = if (query.isBlank()) {
            allCombinedItems
        } else {
            val lowerCaseQuery = query.lowercase()
            allCombinedItems.filter { item ->
                when (item) {
                    is TimelineItem.Ticket -> {
                        item.data.title.lowercase().contains(lowerCaseQuery) ||
                                item.data.actor.lowercase().contains(lowerCaseQuery) ||
                                item.data.place.lowercase().contains(lowerCaseQuery) ||
                                item.data.tags.any { tag -> tag.lowercase().contains(lowerCaseQuery) }
                    }
                    is TimelineItem.Review -> {
                        // 口コミの検索対象プロパティ
                        item.data.title.lowercase().contains(lowerCaseQuery) ||
                                item.data.comment.lowercase().contains(lowerCaseQuery) ||
                                item.data.userName.lowercase().contains(lowerCaseQuery)
                    }
                }
            }
        }

        _timelineItems.value = filteredList
        Log.d("TimelineViewModel", "リアルタイム検索実行: ${filteredList.size}件表示")
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

    private val reviewRepository = com.practice.astra.repository.ReviewRepository()

    fun toggleReviewLike(review: ReviewData) {
        viewModelScope.launch {
            try {
                reviewRepository.toggleLike(review.id, review.likedBy)
                loadTimeline() // 全体リロード
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "いいね失敗", e)
            }
        }
    }
}