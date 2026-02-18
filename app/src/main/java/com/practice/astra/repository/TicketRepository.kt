package com.practice.astra.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TicketRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    suspend fun toggleBookmark(ticketId: String) {
        if (currentUserId.isEmpty()) return

        try {
            val userRef = db.collection("users").document(currentUserId)
            val doc = userRef.get().await()

            // bookmark配列をチェック
            val bookmarkList = doc.get("bookmark") as? List<String> ?: emptyList()

            if (bookmarkList.contains(ticketId)) {
                userRef.update("bookmark", FieldValue.arrayRemove(ticketId)).await()
                Log.d("REPO_DEBUG", "bookmarkから削除: $ticketId")
            } else {
                userRef.update("bookmark", FieldValue.arrayUnion(ticketId)).await()
                Log.d("REPO_DEBUG", "bookmarkに追加: $ticketId")
            }
        } catch (e: Exception) {
            Log.e("REPO_DEBUG", "bookmark操作失敗", e)
            throw e
        }
    }

    suspend fun isBookmarked(ticketId: String): Boolean {
        if (currentUserId.isEmpty()) return false
        return try {
            val doc = db.collection("users").document(currentUserId).get().await()
            val recommendList = doc.get("recommend") as? List<String> ?: emptyList()
            recommendList.contains(ticketId)
        } catch (e: Exception) {
            false
        }
    }
}