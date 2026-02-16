package com.practice.astra.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TicketRepository {
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = "test_user_001"

    suspend fun toggleBookmark(ticketId: String) {
        try {
            val bookmarkDocRef = db.collection("users").document(currentUserId)
                .collection("bookmarks").document(ticketId)

            val doc = bookmarkDocRef.get().await()
            if (doc.exists()) {
                bookmarkDocRef.delete().await()
                Log.d("REPO_DEBUG", "ブックマーク解除: $ticketId")
            } else {
                val data = mapOf("bookmarkedAt" to com.google.firebase.Timestamp.now())
                bookmarkDocRef.set(data).await()
                Log.d("REPO_DEBUG", "ブックマーク登録: $ticketId")
            }
        } catch (e: Exception) {
            Log.e("REPO_DEBUG", "操作失敗", e)
            throw e
        }
    }

    suspend fun isBookmarked(ticketId: String): Boolean {
        return try {
            val doc = db.collection("users").document(currentUserId)
                .collection("bookmarks").document(ticketId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }
}