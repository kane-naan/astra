package com.practice.astra.ui.followList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import android.view.ViewGroup
import com.practice.astra.databinding.FragmentBaseUserListBinding
import com.practice.astra.ui.base.BaseUserListFragment

class FollowerListFragment : BaseUserListFragment() {

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
        viewModel.loadUsers(isFollowing = false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}