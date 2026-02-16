package com.practice.astra.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.ui.ticket.RecyclerAdapter
import com.practice.astra.data.TicketData
import java.util.ArrayList
import android.util.Log
import androidx.lifecycle.LiveData
import com.practice.astra.util.ListUtils

/**
 * チケット抽象基底クラス
 */
abstract class BaseTicketListFragment : Fragment() {

    // --- 継承先（BookmarkedTicketsFragmentなど）で実装する ---
    protected abstract fun onTicketClicked(ticket: TicketData)
    protected abstract fun onBookmarkClicked(ticket: TicketData)

    private fun setupRecyclerView(
        recyclerView: RecyclerView,
        listData: List<TicketData>,
        onItemClick: (TicketData) -> Unit
    ): RecyclerAdapter {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lateinit var adapter: RecyclerAdapter
        adapter = RecyclerAdapter(
            listData = ArrayList(listData),
            onItemClick = onItemClick,
            onBookmarkClick = { clickedTicket ->
                handleBookmarkToggleAndNotify(clickedTicket, adapter, listData)
                onBookmarkClicked(clickedTicket)
            }
        )
        recyclerView.adapter = adapter
        return adapter
    }

    private fun handleBookmarkToggleAndNotify(
        data: TicketData,
        adapter: RecyclerAdapter,
        list: List<TicketData>
    ) {
        ListUtils.handleToggle(data, adapter, list) { updatedData ->
            Log.d("BaseTicketListFragment", "Firebase更新待機: ${updatedData.title} (ID: ${updatedData.id})")
        }
    }

    private fun syncRecyclerView(
        recyclerView: RecyclerView,
        listData: List<TicketData>,
        onItemClick: (TicketData) -> Unit
    ) {
        val currentAdapter = recyclerView.adapter as? RecyclerAdapter
        if (currentAdapter == null) {
            setupRecyclerView(recyclerView, listData, onItemClick)
        } else {
            currentAdapter.updateData(listData)
        }
    }

    protected fun observeAndSync(
        liveData: LiveData<List<TicketData>>,
        recyclerView: RecyclerView
    ) {
        liveData.observe(viewLifecycleOwner) { list ->
            syncRecyclerView(
                recyclerView = recyclerView,
                listData = list,
                onItemClick = { ticket ->
                    Log.d("DEBUG", "BaseTicketList: クリック検知 ${ticket.id}")
                    onTicketClicked(ticket)
                }
            )
        }
    }
}