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

    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails

    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    private val repository = TicketRepository()

    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        if (ticket.price == 0 || ticket.price == null) "無料" else "¥${ticket.price}"
    }

    // チケット詳細
    fun loadTicketDetails(ticketId: String) {
        viewModelScope.launch {
            try{
                val document = db.collection("tickets").document(ticketId).get().await()

                if (document.exists()){
                    val ticket = document.toObject(TicketData::class.java)?.copy(id = document.id)
                    ticket?.let {
                        _selectedTicketDetails.value = it
                        loadOrganizationInfo(it.organizationId)
                        loadOrganizationTickets(it.organizationId)

                        checkBookmarkStatus(it.id)
                    }
                }else{
                    Log.d("TicketDetailViewModel", "指定されたチケットが見つかりません: $ticketId")
                }

            }catch(e:Exception){
                Log.e("TicketDetailViewModel","詳細データの取得に失敗しました", e)
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
                val snapshot = db.collection("tickets")
                    .whereEqualTo("organizationId", organizationId)
                    .get()
                    .await()

                val tickets = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(id = doc.id)
                }
                _organizationTickets.value = tickets
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "団体チケット取得エラー", e)
            }
        }
    }

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked
    private val currentUserId = "test_user_001"

    fun checkBookmarkStatus(ticketId: String){
        viewModelScope.launch {
            try {
                Log.d("DEBUG_ASTRA", "=== 接続テスト開始 ===")

                val doc = db.collection("users").document(currentUserId)
                    .collection("bookmarks").document(ticketId).get().await()

                if (doc.exists()) {
                    Log.d("DEBUG_ASTRA", "【確証】接続成功！保存されているデータ: ${doc.data}")
                    _isBookmarked.postValue(true)
                } else {
                    Log.d("DEBUG_ASTRA", "【確証】接続成功！ただしデータは空です。")
                    _isBookmarked.postValue(false)
                }

            } catch (e: Exception) {
                Log.e("DEBUG_ASTRA", "【失敗】接続エラーが発生しました: ${e.message}", e)
                _isBookmarked.postValue(false)
            }
        }
    }

    fun toggleBookmark(ticketId: String){
        val isCurrentlyBookmarked = _isBookmarked.value ?: false
        viewModelScope.launch {
            try {
                Log.d("DEBUG_ASTRA", "通信開始: $ticketId (現在の状態: $isCurrentlyBookmarked)")

                val bookmarkDocRef = db.collection("users").document(currentUserId)
                    .collection("bookmarks").document(ticketId)

                if (isCurrentlyBookmarked) {
                    // ブックマーク解除
                    bookmarkDocRef.delete().await()
                    Log.d("DEBUG_ASTRA", "Firestoreから削除成功")
                    _isBookmarked.postValue(false)
                } else {
                    // ブックマーク登録
                    val data = mapOf("bookmarkedAt" to com.google.firebase.Timestamp.now())
                    bookmarkDocRef.set(data).await()

                    Log.d("DEBUG_ASTRA", "Firestoreへ保存成功！")
                    _isBookmarked.postValue(true)
                }
            } catch (e: Exception) {
                Log.e("DEBUG_ASTRA", "Firestore操作失敗: ${e.message}", e)
            }
        }
    }
}