package com.practice.astra.data

data class TicketData(
    override val id: String,
    val title:String,
    val image:Int,
    val actor:String,
    val place:String,
    val price:Int?,
    override var isToggled: Boolean
) : ToggleableItem
