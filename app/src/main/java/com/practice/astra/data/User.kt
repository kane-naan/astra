package com.practice.astra.data

data class User(
    val name: String,
    val message: String,
    val header: Int,
    val icon: Int,
    val recommend: List<String>, // チケットのタイトル
    val distribution: List<TicketData>,
    val writing: List<Review>,
    var follower: Int,
    var following: Int
)
