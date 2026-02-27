package com.practice.astra.data

import com.google.firebase.Timestamp

data class ReviewData(
    val id: String = "", // 口コミ自体のID
    val ticketId: String = "", // どのチケットに対する口コミか
    val userId: String = "", // 投稿したユーザーのID
    val organizationId: String = "", // 団体ID
    val userName: String = "匿名ユーザー", // 表示名（ユーザー情報の取得を減らすため保持）
    val userIconUrl: String = "", // アイコン画像URL
    val rating: Float = 0f, // 評価
    val title: String = "", // 口コミのタイトル
    val comment: String = "", // 口コミの本文
    val likeCount: Int = 0, // いいねの数
    val likedBy: List<String> = emptyList(), // いいねしたユーザーIDのリスト
    val createdAt: Timestamp? = null, // 投稿日時
)
