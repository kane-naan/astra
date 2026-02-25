package com.practice.astra.ui.followList

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.practice.astra.R
import com.practice.astra.data.User
import com.practice.astra.data.UserList
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserListViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userList = MutableLiveData<List<UserList>>()
    val userList: LiveData<List<UserList>> = _userList

    fun loadUsers(isFollowing: Boolean) {
        val currentUserUid = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                val myDoc = db.collection("users").document(currentUserUid).get().await()
                val myUserData = myDoc.toObject(User::class.java) ?: return@launch
                val targetIds = if (isFollowing) myUserData.following else myUserData.follower

                if (targetIds.isEmpty()) {
                    _userList.postValue(emptyList())
                    return@launch
                }

                val usersSnapshot = db.collection("users")
                    .whereIn(FieldPath.documentId(), targetIds)
                    .get()
                    .await()

                val displayList = usersSnapshot.documents.mapNotNull { doc ->
                    val user = doc.toObject(User::class.java)
                    user?.let {
                        UserList(
                            id = doc.id,
                            userName = it.name,
                            image = R.drawable.user_icon,
                            message = it.message,
                            isToggled = myUserData.following.contains(doc.id)
                        )
                    }
                }

                _userList.postValue(displayList)

            } catch (e: Exception) {
                Log.e("UserListViewModel", "ユーザーリスト取得失敗", e)
                _userList.postValue(emptyList())
            }
        }
    }

    fun toggleFollow(targetUser: UserList) {

    }
}