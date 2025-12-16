package com.practice.astra.ui.followList

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practice.astra.R
import com.practice.astra.data.UserList
import com.practice.astra.databinding.UserListItemBinding

class UserViewHolder(val binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: UserList) {
        binding.userImage.setImageResource(data.image)
        binding.userName.text = data.userName
        binding.userMessage.text = data.message

        // フォロー状態に応じてボタンのテキストとスタイルを切り替える
        if (data.follow) {
            binding.followButton.text = "フォロー中"
            binding.followButton.setTextColor(binding.root.context.getColor(R.color.blueGray))
        } else {
            binding.followButton.text = "フォロー"
            binding.followButton.setTextColor(binding.root.context.getColor(R.color.white))
        }
    }
}

class UserAdapter(
    private val listData: ArrayList<UserList>,
    private val onUserClick: (UserList) -> Unit, // ユーザーアイテム全体クリック
    private val onFollowToggle: (UserList) -> Unit // フォローボタンクリック
) : RecyclerView.Adapter<UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = listData[position]
        holder.bind(user)

        // ユーザーアイテムがクリックされた時の処理
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }

        // フォローボタンがクリックされた時の処理
        holder.binding.followButton.setOnClickListener {
            onFollowToggle(user)
        }
    }
}