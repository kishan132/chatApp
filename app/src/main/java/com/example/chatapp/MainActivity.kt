package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.SampleFragment
import com.example.chatapp.ui.UserFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    lateinit var binding: ActivityMainBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        openSampleFragment()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {

                when(tab?.position){
                    0 -> {
                        openSampleFragment()
                    }
                    1->{
                        openUserFragment()
                    }
                }

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }


        })


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            //user will be logout
            Log.d(TAG, "onOptionsItemSelected: user signOut")
            auth.signOut()
            startActivity(Intent(this,PhoneActivity::class.java))
            finish()
        }

        return true
    }

    private fun openSampleFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder,SampleFragment())
            .commit()
    }

    private fun openUserFragment(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentHolder,UserFragment())
            .commit()
    }

}