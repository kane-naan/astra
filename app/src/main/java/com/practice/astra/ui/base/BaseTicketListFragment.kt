package com.practice.astra.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.RecyclerAdapter
import com.practice.astra.data.TicketData
import java.util.ArrayList
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.practice.astra.R

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
    ):RecyclerAdapter{
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
        data.bookmark = !data.bookmark
        Log.d("BaseFragment", "共通ブックマーククリック: ${data.title} -> 新しい状態: ${data.bookmark}")
        val index = list.indexOf(data)
        if (index != -1) {
            adapter.notifyItemChanged(index)
        }
    }
}