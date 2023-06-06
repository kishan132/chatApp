package com.example.chatapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.databinding.ActivityOtpactivityBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class OTPActivity : AppCompatActivity() {

    private val TAG = "OTPActivity"
    lateinit var binding: ActivityOtpactivityBinding
    private lateinit var phoneNumber: String

    lateinit var progressDialog: ProgressDialog
    lateinit var auth: FirebaseAuth
    lateinit var verifyOTPCallback: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    var storedVerificationId: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken

    lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        phoneNumber = intent.getStringExtra("PHONE").toString()
        binding.tvInfo.text = "Verify $phoneNumber"

        setTimer(60000)

        auth = Firebase.auth

        binding.btnVerify.setOnClickListener {

            Log.d(
                TAG,
                "onCreate: btnVerify: $storedVerificationId - ${binding.etOTP.text.toString()}"
            )

            val credential =
                PhoneAuthProvider.getCredential(storedVerificationId, binding.etOTP.text.toString())
            signInWithPhoneAuthCredential(credential)
        }

        binding.btnResend.setOnClickListener {
            resendVerificationCode(phoneNumber, resendToken)
            setTimer(60000)
            progressDialog = createDialog("ReSending verification code", false)
            progressDialog.show()
        }

        verifyOTPCallback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                val smsCode: String? = credential.smsCode

                progressDialog.dismiss()

                Log.d(TAG, "onVerificationCompleted: $smsCode")
                binding.etOTP.setText(smsCode)

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.

                Log.d(TAG, "onVerificationFailed: $e")
                progressDialog.dismiss()

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Snackbar.make(binding.root, "Invalid request", Snackbar.LENGTH_SHORT).show()
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Snackbar.make(
                        binding.root,
                        "The SMS quota for the project has been exceeded",
                        Snackbar.LENGTH_SHORT
                    ).show()
                } else {
                    // Verification fail! Check Mobile Number.
                    Snackbar.make(
                        binding.root,
                        "Verification fail! Check Mobile Number.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    notifyUserAndRetry("Verification fail! Check Mobile Number.")
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent: $verificationId")

                progressDialog.dismiss()

                // Save verification ID and resending token so we can use them later
                storedVerificationId = verificationId
                resendToken = token
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {
                super.onCodeAutoRetrievalTimeOut(p0)

                progressDialog.dismiss()
            }

        }

        startPhoneNumberVerification()
        progressDialog = createDialog("Sending verification code", false)
        progressDialog.show()

    }

    private fun setTimer(time: Long) {

        binding.tvTimer.visibility = View.VISIBLE
        binding.btnResend.isEnabled = false

        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(timeLeft: Long) {
                binding.tvTimer.text = "Time Left:  ${timeLeft / 1000}"
            }

            override fun onFinish() {
                binding.tvTimer.visibility = View.GONE
                binding.btnResend.isEnabled = true
            }
        }.start()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success1 ${task.result?.user}")
                    Log.d(TAG, "signInWithCredential:success2 ${task.result?.additionalUserInfo}")

                    if (task.result?.additionalUserInfo?.isNewUser == true) {
                        showSignUpActivity()
                    } else {
                        showHomeActivity()
                    }

                } else {
                    // Sign in failed, display a message and update the UI
                    Log.w(TAG, "signInWithCredential:failure", task.exception)

                    if (progressDialog.isShowing)
                        progressDialog.dismiss()

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                        Snackbar.make(
                            binding.root,
                            "The verification code entered was invalid",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    notifyUserAndRetry("Some Connection Error! Retry Again.")
                }
            }
    }

    private fun notifyUserAndRetry(msg: String) {

        MaterialAlertDialogBuilder(this).apply {
            setMessage(msg)
            setPositiveButton("OK") { _, _ -> showPhoneActivity() }
            setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            setCancelable(false)
            create()
            show()
        }
    }

    private fun showPhoneActivity() {
        Log.d(TAG, "showPhoneActivity: ")

        val intent = Intent(this, PhoneActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showSignUpActivity() {

        Log.d(TAG, "showSignUpActivity: ")
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showHomeActivity() {
        Log.d(TAG, "showHomeActivity: ")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun resendVerificationCode(
        phoneNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?,
    ) {
        val optionsBuilder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // (optional) Activity for callback binding
            // If no activity is passed, reCAPTCHA verification can not be used.
            .setCallbacks(verifyOTPCallback) // OnVerificationStateChangedCallbacks
        if (token != null) {
            optionsBuilder.setForceResendingToken(token) // callback's ForceResendingToken
        }
        PhoneAuthProvider.verifyPhoneNumber(optionsBuilder.build())
    }

    private fun startPhoneNumberVerification() {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setActivity(this)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setCallbacks(verifyOTPCallback)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // to restrict user go back
    override fun onBackPressed() {
        Snackbar.make(binding.root, "Can't go back Process will Stop", Snackbar.LENGTH_SHORT).show()
    }

}