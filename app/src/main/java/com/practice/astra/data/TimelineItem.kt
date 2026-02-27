package com.practice.astra.data

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class TimelineItem {
    data class Ticket(val data: TicketData) : TimelineItem()
    data class Review(val data: ReviewData) : TimelineItem()

    fun getSortingDate(): Date? {
        return when (this) {
            is Ticket -> {
                val formatter = SimpleDateFormat("yyyy年M月d日 H:mm～", Locale.JAPANESE)
                try {
                    formatter.parse(data.event_date)
                } catch (e: Exception) {
                    Log.e("TimelineItem", "日付パース失敗: ${data.event_date}")
                    null
                }
            }
            is Review -> {
                data.createdAt?.toDate()
            }
        }
    }
}