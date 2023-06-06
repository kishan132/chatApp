package com.example.chatapp.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.dataClass.User
import com.example.chatapp.adapter.UserAdapter
import com.example.chatapp.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserFragment : Fragment() {

    private val TAG = "PeopleFragment"
    private var binding: FragmentUserBinding?= null
    private lateinit var auth: FirebaseAuth
    private lateinit var peopleAdapter: UserAdapter
    private var peopleList: MutableList<User> = mutableListOf()

    // for fireStore database
    private lateinit var database: FirebaseFirestore
    // for realtime database
    private lateinit var mDBRef: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false)

        auth = Firebase.auth

        database = Firebase.firestore
        mDBRef = FirebaseDatabase.getInstance().reference

        peopleAdapter = UserAdapter(requireContext())

        binding!!.rvPeople.apply{
            layoutManager = LinearLayoutManager(context)
            adapter = peopleAdapter
        }

        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        /*database.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "success documents: ")
                for (document in documents) {
                    val currentUser = document.toObject(User::class.java)
                    peopleList.add(currentUser)
                }
                Log.d(TAG, "success documents: $peopleList")
                peopleAdapter.submitList(peopleList)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }*/

        mDBRef.child("users").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                Log.d(TAG, "success documents: ")

                peopleList.clear()
                for(document in snapshot.children){
                    val currentUser : User = document.getValue(User::class.java)!!

                    // to avoid current user name in list
                    if(auth.currentUser?.uid!=currentUser.id)
                        peopleList.add(currentUser)

                }
                Log.d(TAG, "success documents: $peopleList")
                peopleAdapter.submitList(peopleList)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.d(TAG, "listener failed to connect DB")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}