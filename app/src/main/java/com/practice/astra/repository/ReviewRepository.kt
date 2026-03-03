package com.practice.astra.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ReviewRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /**
     * 口コミのいいね状態を切り替える
     */
    suspend fun toggleLike(reviewId: String, currentLikedBy: List<String>) {
        val userId = auth.currentUser?.uid ?: return
        val reviewRef = db.collection("reviews").document(reviewId)

        val isLiked = currentLikedBy.contains(userId)

        if (isLiked) {
            // すでにいいねしている場合：解除
            reviewRef.update(
                "likeCount", FieldValue.increment(-1),
                "likedBy", FieldValue.arrayRemove(userId)
            ).await()
        } else {
            // まだいいねしていない場合：登録
            reviewRef.update(
                "likeCount", FieldValue.increment(1),
                "likedBy", FieldValue.arrayUnion(userId)
            ).await()
        }
    }
}