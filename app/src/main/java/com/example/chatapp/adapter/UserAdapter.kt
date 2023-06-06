package com.example.chatapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.dataClass.User
import com.example.chatapp.databinding.UserItemBinding

class UserAdapter(val context: Context) : ListAdapter<User, UserAdapter.UserViewHolder>(
    object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        /*return UserViewHolder(parent.context.getSystemService(LayoutInflater::class.java).inflate(
            R.layout.people_view,parent,false))*/

        val view: View = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        UserItemBinding.bind(holder.itemView).apply {

            val user = getItem(position)

            tvUsername.text = user.name
            Glide.with(this.root).load(user.imageUrl).circleCrop().into(avatar)

            root.setOnClickListener {
                openChatActivity(user)
            }

        }
    }

    private fun openChatActivity(user: User) {
        val intent = Intent(context,ChatActivity::class.java)
        intent.putExtra("USER_NAME",user.name)
        intent.putExtra("USER_IMAGE",user.imageUrl)
        intent.putExtra("USER_ID",user.id)
        context.startActivity(intent)

    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}