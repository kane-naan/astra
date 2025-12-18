package com.practice.astra.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.ui.ticket.RecyclerAdapter
import com.practice.astra.data.TicketData
import java.util.ArrayList
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.practice.astra.R
import com.practice.astra.util.ListUtils

/**
 * チケット抽象基底クラス
 * チケットを表示する際にこのFragmentを継承する
 *
 * getRecyclerView:リサイクラービュー作成
 * generateListData:チケットアイテムのリスト
 * handleItemClisk:チケットアイテムがクリックされた時の処理
 * handleBookmarkClisk:ブックマークがクリックされた時の処理
 *
 * */
abstract class BaseTicketListFragment : Fragment() {

    /**
     * setupRecyclerView
     * 戻り値:RecyclerAdapter
     *
     * Adapterが呼び出し元に2つある場合、片方がオーバーライドされないよう
     * 戻り値を設定
     * */
    protected fun setupRecyclerView(
        recyclerView: RecyclerView,
        listData: List<TicketData>,
        onItemClick:(TicketData) -> Unit
    ): RecyclerAdapter {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lateinit var adapter: RecyclerAdapter
        adapter = RecyclerAdapter(
            listData = ArrayList(listData),
            onItemClick = onItemClick,
            onBookmarkClick = { clickedTicket ->
                handleBookmarkToggleAndNotify(clickedTicket, adapter, listData)
            }
        )
        recyclerView.adapter = adapter
        return adapter
    }

    protected fun commonHandleItemClick(data: TicketData){
        Log.d("BaseFragment", "共通アイテムクリック:${data.title}")
        findNavController().navigate(
            R.id.ticketDetailFragment,
            Bundle().apply {
                // putParcelable("ticket_data", data)
            }
        )
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
}