package com.practice.astra.data

import com.practice.astra.ui.common.ToggleableItem

data class TicketData(
    override val id: String = "",
    val title: String = "",
    val imageUrl: String = "",
    val actor: String = "",
    val place: String = "",
    val price: Int? = null,
    val description: String = "", // 内容
    val event_date: String = "",  // 日時
    val point: String = "",       // 見どころ
    val organizationId: String = "",
    val tags: List<String> = emptyList(),
    override var isToggled: Boolean = false
) : ToggleableItem