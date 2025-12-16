package com.practice.astra.ui.followList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.R
import com.practice.astra.data.UserList
import com.practice.astra.databinding.FragmentBaseUserListBinding
import com.practice.astra.ui.base.BaseUserListFragment

class FollowingListFragment : BaseUserListFragment() {

    private var _binding: FragmentBaseUserListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUserRecyclerView(
            recyclerView = binding.userListRecyclerView,
            listData = generateFollowingListData(),
            onUserClick = { userList -> commonHandleUserClick(userList) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateFollowingListData(): List<UserList> {
        return listOf(
            UserList("ユーザー A", R.drawable.user_icon, "Hello from A", true),
            UserList("ユーザー B", R.drawable.user_icon, "Bのメッセージ", true),
            UserList("ユーザー C", R.drawable.user_icon, "Cだよ", true),
            UserList("ユーザー D", R.drawable.user_icon, "新着!", false)
        )
    }
}