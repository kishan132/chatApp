package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class SplashActivity : AppCompatActivity() {

    private val TAG = "SplashActivity"
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth  = Firebase.auth

        Log.d(TAG, "onCreate: SplashActivity ${auth.uid}")
        Log.d(TAG, "onCreate: SplashActivity ${auth.currentUser}")

        if(auth.currentUser ==null)
            startActivity(Intent(this,PhoneActivity::class.java))
        else
            startActivity(Intent(this,MainActivity::class.java))

        finish()

    }
}