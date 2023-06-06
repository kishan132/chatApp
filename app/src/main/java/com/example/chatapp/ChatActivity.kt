package com.example.chatapp

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.adapter.ChatAdapter
import com.example.chatapp.dataClass.Message
import com.example.chatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private val TAG = "ChatActivity"

    private lateinit var binding: ActivityChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatList: MutableList<Message> = mutableListOf()

    var senderRoom: String? = null
    var receiverRoom: String? = null

    // for realtime database
    private lateinit var mDBRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receiverName = intent.getStringExtra("USER_NAME")!!
        val receiverImage = intent.getStringExtra("USER_IMAGE")!!
        val receiverID = intent.getStringExtra("USER_ID")!!

        supportActionBar?.title = receiverName

        mDBRef = FirebaseDatabase.getInstance().reference

        // will create room to make chat private
        // can only be access by receiver and sender(current user)
        val senderID: String = FirebaseAuth.getInstance().currentUser?.uid!!
        receiverRoom = senderID + receiverID
        senderRoom = receiverID + senderID

        chatAdapter = ChatAdapter(this)

        binding.rvChat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }

        //update realtime data in recycler view
        mDBRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d(TAG, "onDataChange: success")

                    chatList.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        chatList.add(message!!)
                    }
                    chatAdapter.submitList(chatList)
                    chatAdapter.notifyDataSetChanged()

                    if (chatList.isNotEmpty())
                        binding.rvChat.smoothScrollToPosition(chatList.size - 1)

                    Log.d(TAG, "onDataChange: success $chatList")
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d(TAG, "onCancelled: database error")
                }

            })

        binding.ivSend.setOnClickListener {

            val msg: String = binding.etMsgBox.text.toString()

            if (msg.trim().isEmpty()) {
                Toast.makeText(this, "Please Enter message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val message = Message(senderID = senderID, msg = msg)

            mDBRef.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
                .addOnSuccessListener {
                    mDBRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(message)
                }

            binding.etMsgBox.text.clear()
        }


    }
}