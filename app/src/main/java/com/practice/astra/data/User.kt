package com.practice.astra.data

data class User(
    val name: String = "",
    val message: String = "",
    val header: Int = 0,
    val icon: Int = 0,
    val recommend: List<String> = emptyList(),
    val distribution: List<TicketData> = emptyList(),
    val writing: List<String> = emptyList(),
    var follower: List<String> = emptyList(),
    var following: List<String> = emptyList(),
    val bookmark: List<String> = emptyList(),
)
