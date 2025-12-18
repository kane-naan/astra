package com.practice.astra.util

import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.data.ToggleableItem

object ListUtils {
    fun <T : ToggleableItem> handleToggle(
        data: T,
        adapter: RecyclerView.Adapter<*>,
        list: List<T>,
        onFirebaseUpdate: (T) -> Unit = {}
    ) {
        data.isToggled = !data.isToggled

        val index = list.indexOf(data)
        if (index != -1) {
            adapter.notifyItemChanged(index)
        }
        onFirebaseUpdate(data)
    }
}