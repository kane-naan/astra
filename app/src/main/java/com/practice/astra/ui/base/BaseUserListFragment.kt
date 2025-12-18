package com.practice.astra.ui.base

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.R
import com.practice.astra.data.UserList
import com.practice.astra.ui.followList.UserAdapter
import com.practice.astra.util.ListUtils

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
        findNavController().navigate(R.id.navigation_user)
        // val bundle = Bundle().apply { putString("user_id", data.userName) }
        // findNavController().navigate(R.id.navigation_user, bundle)
    }

    private fun handleFollowToggleAndNotify(
        data: UserList,
        adapter: UserAdapter,
        list: List<UserList>
    ) {
        ListUtils.handleToggle(data, adapter, list) { updatedData ->
            Log.d("BaseUserFragment", "Firebase更新待機: ${updatedData.userName} (ID: ${updatedData.id})")
        }
    }
}