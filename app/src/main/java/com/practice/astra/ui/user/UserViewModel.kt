package com.practice.astra.ui.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.data.TicketData
import com.practice.astra.data.User
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import com.practice.astra.repository.TicketRepository

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _myReviews = MutableLiveData<List<com.practice.astra.data.ReviewData>>()
    val myReviews: LiveData<List<com.practice.astra.data.ReviewData>> = _myReviews

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    private val _recommendedTickets = MutableLiveData<List<TicketData>>()
    val recommendedTickets: LiveData<List<TicketData>> = _recommendedTickets

    private val _isMyProfile = MutableLiveData<Boolean>()
    val isMyProfile: LiveData<Boolean> = _isMyProfile

    private val repository = TicketRepository()

    // 遷移先のユーザーIDを保持
    private var targetUserId: String? = null

    fun setTargetUserId(userId: String?) {
        this.targetUserId = userId
    }

    fun loadUserData() {
        val currentUid = auth.currentUser?.uid ?: return
        val userIdToFetch = targetUserId ?: currentUid

        viewModelScope.launch {
            try {
                // ★修正：userIdToFetchを使ってデータを取得する
                val doc = db.collection("users").document(userIdToFetch).get().await()
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        _userData.postValue(user)

                        _isMyProfile.postValue(userIdToFetch == currentUid)

                        // おすすめチケットの取得
                        if (user.recommend.isNotEmpty()) {
                            // ユーザーのブックマーク情報を渡す必要がある
                            fetchTicketsByIds(user.recommend, user.bookmark)
                        } else {
                            _recommendedTickets.postValue(emptyList())
                        }
                        loadMyReviews(userIdToFetch)
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "ユーザーデータ取得失敗", e)
            }
        }
    }

    /**
     * チケットIDのリストを受け取って、実際のチケットデータを取得する
     */
    private suspend fun fetchTicketsByIds(ids: List<String>, myBookmarks: List<String>) {
        try {
            val snapshot = db.collection("tickets")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .await()

            val tickets = snapshot.documents.mapNotNull { doc ->
                val ticket = doc.toObject(TicketData::class.java)
                ticket?.copy(
                    id = doc.id,
                    // ★修正：渡された myBookmarks を使用
                    isToggled = myBookmarks.contains(doc.id)
                )
            }
            _recommendedTickets.postValue(tickets)
        } catch (e: Exception) {
            Log.e("UserViewModel", "チケット詳細の取得に失敗", e)
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                Log.d("UserViewModel", "リポジトリ経由で保存/削除に成功: $ticketId")
                loadUserData()
            } catch (e: Exception) {
                Log.e("UserViewModel", "リポジトリ呼び出し失敗", e)
            }
        }
    }

    private fun loadMyReviews(userId: String) {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("reviews")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val reviews = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(com.practice.astra.data.ReviewData::class.java)
                }
                _myReviews.postValue(reviews)
            } catch (e: Exception) {
                Log.e("UserViewModel", "口コミ取得失敗", e)
            }
        }
    }
}