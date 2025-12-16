package com.practice.astra.ui.followList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.practice.astra.R
import com.practice.astra.data.UserList
import com.practice.astra.databinding.FragmentBaseUserListBinding
import com.practice.astra.ui.base.BaseUserListFragment

class FollowerListFragment : BaseUserListFragment() {

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
            listData = generateFollowerListData(),
            onUserClick = { userList -> commonHandleUserClick(userList) }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateFollowerListData(): List<UserList> {
        return listOf(
            UserList("ユーザー X", R.drawable.user_icon, "Xがフォローしています。", true), // Xは相互フォロー（true）
            UserList("ユーザー Y", R.drawable.user_icon, "Yの近況", false), // Yは一方的にフォローしている（false）
            UserList("ユーザー Z", R.drawable.user_icon, "Zです", true),
        )
    }
}