package com.practice.astra.data

data class UserList(
    override val id: String,
    val userName: String,
    val image: Int,
    val message: String,
    override var isToggled: Boolean
):ToggleableItem
