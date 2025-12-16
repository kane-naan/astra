package com.practice.astra.ui.base

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.data.UserList
import com.practice.astra.ui.followList.UserAdapter

/**
 * フォロー/フォロワーリストの抽象基底クラス
 */
abstract class BaseUserListFragment : Fragment() {

    protected fun setupUserRecyclerView(
        recyclerView: RecyclerView,
        listData: List<UserList>,
        onUserClick: (UserList) -> Unit
    ): UserAdapter {
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        lateinit var adapter: UserAdapter

        adapter = UserAdapter(
            listData = ArrayList(listData),
            onUserClick = onUserClick,
            onFollowToggle = { clickedUser ->
                handleFollowToggleAndNotify(clickedUser, adapter, listData)
            }
        )
        recyclerView.adapter = adapter
        return adapter
    }

    protected fun commonHandleUserClick(data: UserList) {
        Log.d("BaseUserFragment", "ユーザーアイテムクリック: ${data.userName}")
    }

    private fun handleFollowToggleAndNotify(
        data: UserList,
        adapter: UserAdapter,
        list: List<UserList>
    ) {
        data.follow = !data.follow

        Log.d("BaseUserFragment", "共通フォローボタンクリック: ${data.userName} -> 新しい状態: ${data.follow}")

        val index = list.indexOf(data)
        if (index != -1) {
            adapter.notifyItemChanged(index)
        }
    }
}