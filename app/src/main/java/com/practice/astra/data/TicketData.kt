package com.practice.astra.data

import com.practice.astra.ui.common.ToggleableItem

data class TicketData(
    override val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val actor: String = "",
    val place: String = "",
    val price: Int? = null,
    val tags: List<String> = emptyList(),
    override var isToggled: Boolean = false
) : ToggleableItem