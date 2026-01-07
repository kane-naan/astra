package com.practice.astra.data

import com.practice.astra.ui.common.ToggleableItem

data class UserList(
    override val id: String,
    val userName: String,
    val image: Int,
    val message: String,
    override var isToggled: Boolean
): ToggleableItem
