package com.example.chatapp.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.dataClass.Message
import com.example.chatapp.R
import com.example.chatapp.databinding.LayoutReceiveItemBinding
import com.example.chatapp.databinding.LayoutSendItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ChatAdapter(val context:Context) :
    ListAdapter<Message, RecyclerView.ViewHolder>(object : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.toString() == newItem.toString()
        }

    }) {

    private val TAG="ChatAdapter"
    private val ITEM_SENT = 1
    private val ITEM_RECEIVE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        Log.d(TAG, "onCreateViewHolder: $viewType")
        
        return if(viewType == ITEM_SENT){
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_send_item,parent,false)
            SendViewHolder(view)
        } else{
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_receive_item,parent,false)
            ReceiveViewHolder(view)
        }

    }

    override fun getItemViewType(position: Int): Int {
        val currentUser = getItem(position)
        Log.d(TAG, "getItemViewType: $currentUser")

        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentUser.senderID)){
            ITEM_SENT
        } else ITEM_RECEIVE
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMsg = getItem(position)
        Log.d(TAG, "onBindViewHolder: $currentMsg")

        if(holder.javaClass == SendViewHolder::class.java){
            val sendHolder = holder as SendViewHolder
            //sendHolder.sendMsg.text = currentMsg.msg
            LayoutSendItemBinding.bind(sendHolder.itemView).apply {
                sendTextMsg.text = currentMsg.msg
            }
        }
        else{
            val receiveHolder = holder as ReceiveViewHolder
            //receiveHolder.receiveMsg.text = currentMsg.msg
            LayoutReceiveItemBinding.bind(receiveHolder.itemView).apply {
                receiveTextMsg.text = currentMsg.msg
            }
        }
    }

    class SendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
   /* {
        val sendMsg = itemView.findViewById<TextView>(R.id.sendTextMsg)
    }*/

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    /*{
        val receiveMsg = itemView.findViewById<TextView>(R.id.receiveTextMsg)
    }*/

}