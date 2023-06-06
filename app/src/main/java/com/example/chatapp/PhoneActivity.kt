package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import com.example.chatapp.databinding.ActivityPhoneBinding

class PhoneActivity : AppCompatActivity() {

    lateinit var binding: ActivityPhoneBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etPhoneNbr.addTextChangedListener {
            binding.btnNext.isEnabled = it?.length==10
        }

        binding.btnNext.setOnClickListener {
            val countryCode:String = binding.countryCode.selectedCountryCodeWithPlus
            val phoneNumber: String = countryCode + binding.etPhoneNbr.text.toString()

            val intent = Intent(this,OTPActivity::class.java)
            intent.putExtra("PHONE",phoneNumber)

            startActivity(intent)
            finish()
        }

    }
}