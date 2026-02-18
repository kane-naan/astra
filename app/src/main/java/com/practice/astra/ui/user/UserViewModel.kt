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

    private val _userData = MutableLiveData<User>()
    val userData: LiveData<User> = _userData

    private val _recommendedTickets = MutableLiveData<List<TicketData>>()
    val recommendedTickets: LiveData<List<TicketData>> = _recommendedTickets

    private val repository = TicketRepository()

    fun loadUserData() {
        val currentUser = auth.currentUser ?: return

        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(currentUser.uid).get().await()
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        _userData.postValue(user)

                        if (user.recommend.isNotEmpty()) {
                            fetchTicketsByIds(user.recommend, user.bookmark)
                        } else {
                            _recommendedTickets.postValue(emptyList())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("UserViewModel", "ユーザーデータまたはチケット取得失敗", e)
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
                Log.d("TimelineViewModel", "リポジトリ経由で保存/削除に成功: $ticketId")
                loadUserData()
            } catch (e: Exception) {
                Log.e("TimelineViewModel", "リポジトリ呼び出し失敗", e)
            }
        }
    }
}