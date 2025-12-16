package com.practice.astra.data

data class TicketData(
    val title:String,
    val image:Int,
    val actor:String,
    val place:String,
    val price:Int?,
    var bookmark:Boolean
)
