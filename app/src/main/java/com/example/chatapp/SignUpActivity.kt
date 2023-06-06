package com.example.chatapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.chatapp.dataClass.User
import com.example.chatapp.databinding.ActivitySignUpBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage

class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignUpActivity"
    lateinit var binding: ActivitySignUpBinding

    lateinit var auth: FirebaseAuth

    var downloadUrl: String = ""
    var userName: String = ""

    // for fireStore database
    private lateinit var database: FirebaseFirestore
    // for realtime database
    private lateinit var mDBRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.firestore
        mDBRef = FirebaseDatabase.getInstance().reference

        binding.avatar.setOnClickListener {
            openGallery()
        }

        binding.btnSubmit.setOnClickListener {

            userName = binding.etName.text.toString()

            if (downloadUrl.isEmpty()) {
                Toast.makeText(this, "NO Url found", Toast.LENGTH_SHORT).show()
            } else if (userName.isEmpty()) {
                Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show()
            } else {

                val user = User(userName, downloadUrl, auth.currentUser?.uid!!)
                //or val user = User(userName, downloadUrl, auth.uid!!)

                //insert into fireStore database
               /* database.collection("users").document(auth.uid!!)
                    .set(user)
                    .addOnSuccessListener {
                        showHomeActivity()
                    }.addOnFailureListener {
                        Log.d(TAG, "onCreate: database addition failure")
                    }*/

                // insert into realtime database
                mDBRef.child("users").child(auth.uid!!).setValue(user).addOnCompleteListener {task->
                    if(task.isSuccessful){
                        Log.d(TAG, "realtime database success")
                        showHomeActivity()
                    }
                    else
                        Log.d(TAG, "realtime database fail")
                }

            }
        }

    }

    private fun showHomeActivity() {
        Log.d(TAG, "showHomeActivity: ")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // to restrict user go back
    override fun onBackPressed() {
        Snackbar.make(binding.root, "Can't go back Process will Stop", Snackbar.LENGTH_SHORT).show()
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_PICK
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            intent?.data.let {
                Log.d(TAG, "onActivityResult: $it")
                binding.avatar.setImageURI(it)
                uploadImage(it)
            }
        }
    }

    private fun uploadImage(imageUri: Uri?) {

        binding.btnSubmit.isEnabled = false

        val ref = Firebase.storage.reference.child("uploads/${auth.uid.toString()}")
        val uploadTask = ref.putFile(imageUri!!)

        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->

            if (!task.isSuccessful) {
                Log.d(TAG, "uploadImage: task failed")
            }

            return@Continuation ref.downloadUrl
        }).addOnCompleteListener { task ->

            binding.btnSubmit.isEnabled = true

            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
                Log.d(TAG, "uploadImage: $downloadUrl")
            }

        }.addOnFailureListener {
            binding.btnSubmit.isEnabled = true
        }

        // this is other way
        /*val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "uploadImage: task failed")
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUrl = task.result.toString()
            } else {
                // Handle failures
                // ...
            }
        }*/

    }
}