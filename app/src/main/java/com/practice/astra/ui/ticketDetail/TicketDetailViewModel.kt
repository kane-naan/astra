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
    private val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
    private val repository = TicketRepository()

    private val _selectedTicketDetails = MutableLiveData<TicketData>()
    val selectedTicketDetails: LiveData<TicketData> = _selectedTicketDetails

    private val currentUserId: String get() = auth.currentUser?.uid ?: ""

    private val _organizationTickets = MutableLiveData<List<TicketData>>()
    val organizationTickets: LiveData<List<TicketData>> = _organizationTickets

    private val _isBookmarked = MutableLiveData<Boolean>()
    val isBookmarked: LiveData<Boolean> = _isBookmarked

    private val _isPurchased = MutableLiveData<Boolean>()
    val isPurchased: LiveData<Boolean> = _isPurchased

    private val _purchaseSuccess = MutableLiveData<Boolean>()
    val purchaseSuccess: LiveData<Boolean> = _purchaseSuccess

    val formattedPrice: LiveData<String> = _selectedTicketDetails.map { ticket ->
        if (ticket.price == 0 || ticket.price == null) "無料" else "¥${ticket.price}"
    }

    // チケット詳細
    fun loadTicketDetails(ticketId: String) {
        viewModelScope.launch {
            try {
                val document = db.collection("tickets").document(ticketId).get().await()

                if (document.exists()) {
                    val userDoc = db.collection("users").document(currentUserId).get().await()

                    // ブックマーク状態の確認
                    val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()

                    // 購入済みリストに含まれているか確認
                    val purchasedIds = userDoc.get("purchasedTicketIds") as? List<String> ?: emptyList()
                    _isPurchased.postValue(purchasedIds.contains(ticketId))

                    val ticket = document.toObject(TicketData::class.java)?.copy(
                        id = document.id,
                        isToggled = myBookmarks.contains(document.id)
                    )

                    ticket?.let {
                        _selectedTicketDetails.value = it
                        loadOrganizationInfo(it.organizationId)
                        loadOrganizationTickets(it.organizationId)
                        checkBookmarkStatus(it.id)
                    }
                } else {
                    Log.d("TicketDetailViewModel", "指定されたチケットが見つかりません: $ticketId")
                }
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "詳細データの取得に失敗しました", e)
            }
        }
    }

    private val _organizationDetail = MutableLiveData<OrganizationData>()
    val organizationDetail: LiveData<OrganizationData> = _organizationDetail

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

    fun loadOrganizationTickets(organizationId: String) {
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()
                val snapshot = db.collection("tickets").whereEqualTo("organizationId", organizationId).get().await()
                val tickets = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(TicketData::class.java)?.copy(id = doc.id, isToggled = myBookmarks.contains(doc.id))
                }
                _organizationTickets.value = tickets
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "団体チケット取得エラー", e)
            }
        }
    }

    fun checkBookmarkStatus(ticketId: String) {
        viewModelScope.launch {
            try {
                val userDoc = db.collection("users").document(currentUserId).get().await()
                val myBookmarks = userDoc.get("bookmark") as? List<String> ?: emptyList()
                _isBookmarked.postValue(myBookmarks.contains(ticketId))
            } catch (e: Exception) {
                _isBookmarked.postValue(false)
            }
        }
    }

    fun toggleBookmark(ticketId: String) {
        viewModelScope.launch {
            try {
                repository.toggleBookmark(ticketId)
                val newState = !(_isBookmarked.value ?: false)
                _isBookmarked.postValue(newState)
                val orgId = _selectedTicketDetails.value?.organizationId
                if (orgId != null) loadOrganizationTickets(orgId)
            } catch (e: Exception) {
                Log.e("TicketDetailViewModel", "切り替え失敗", e)
            }
        }
    }

    // チケット購入処理
    fun purchaseTicket(ticketId: String) {
        val uid = currentUserId
        if (uid.isEmpty()) return

        viewModelScope.launch {
            try {
                db.runTransaction { transaction ->
                    val ticketRef = db.collection("tickets").document(ticketId)
                    val userRef = db.collection("users").document(uid)

                    val ticketSnap = transaction.get(ticketRef)
                    val stock = ticketSnap.getLong("stockCount") ?: 0L

                    if (stock <= 0) throw Exception("在庫がありません")

                    // 在庫を減らす
                    transaction.update(ticketRef, "stockCount", stock - 1)

                    // ユーザーの購入リストに追加
                    transaction.update(userRef, "purchasedTicketIds", com.google.firebase.firestore.FieldValue.arrayUnion(ticketId))

                    // 注文履歴を作成
                    val orderRef = db.collection("orders").document()
                    val orderData = mapOf(
                        "uid" to uid,
                        "ticketId" to ticketId,
                        "timestamp" to com.google.firebase.Timestamp.now()
                    )
                    transaction.set(orderRef, orderData)
                }.await()

                Log.d("PURCHASE", "購入成功！")

                // 成功時にLiveDataを更新
                _isPurchased.postValue(true)
                _purchaseSuccess.postValue(true)

                // 最新の情報を再読み込み（在庫数などを反映）
                loadTicketDetails(ticketId)

            } catch (e: Exception) {
                Log.e("PURCHASE", "購入失敗: ${e.message}")
                _purchaseSuccess.postValue(false)
            }
        }
    }
}