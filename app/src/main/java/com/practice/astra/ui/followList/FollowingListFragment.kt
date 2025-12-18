package com.practice.astra.ui.followList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.practice.astra.R
import com.practice.astra.data.UserList
import com.practice.astra.databinding.FragmentBaseUserListBinding
import com.practice.astra.ui.base.BaseUserListFragment

class FollowingListFragment : BaseUserListFragment() {

    private var _binding: FragmentBaseUserListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserListViewModel by viewModels()
    private var adapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.userList.observe(viewLifecycleOwner) { users ->
            if (adapter == null) {
                adapter = setupUserRecyclerView(
                    recyclerView = binding.userListRecyclerView,
                    listData = users,
                    onUserClick = { user -> commonHandleUserClick(user) }
                )
            } else {
                adapter?.updateData(users)
            }
        }
        viewModel.loadUsers(isFollowing = true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}