package com.practice.astra.ui.followList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practice.astra.R
import com.practice.astra.data.UserList

class UserListViewModel : ViewModel() {
    private val _userList = MutableLiveData<List<UserList>>()
    val userList: LiveData<List<UserList>> = _userList

    fun loadUsers(isFollowing: Boolean) {
        if (isFollowing) {
            _userList.value = generateFollowingData()
        } else {
            _userList.value = generateFollowerData()
        }
    }

    fun toggleFollow(user: UserList) {

    }

    private fun generateFollowingData(): List<UserList> {
        return listOf(
            UserList("001", "ユーザー A", R.drawable.user_icon, "Hello from A", true),
            UserList("002", "ユーザー B", R.drawable.user_icon, "Bのメッセージ", true),
            UserList("003", "ユーザー C", R.drawable.user_icon, "Cだよ", true),
            UserList("004", "ユーザー D", R.drawable.user_icon, "新着!", false)
        )
    }
    private fun generateFollowerData(): List<UserList> {
        return listOf(
            UserList("001", "ユーザー A", R.drawable.user_icon, "Hello from A", true),
            UserList("002", "ユーザー B", R.drawable.user_icon, "Bのメッセージ", true),
            UserList("003", "ユーザー C", R.drawable.user_icon, "Cだよ", true),
            UserList("004", "ユーザー D", R.drawable.user_icon, "新着!", false)
        )
    }
}