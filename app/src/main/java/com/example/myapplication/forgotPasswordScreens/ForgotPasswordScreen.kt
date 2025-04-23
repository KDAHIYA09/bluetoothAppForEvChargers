package com.example.myapplication.forgotPasswordScreens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.glance.visibility
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityForgotPasswordScreenBinding
import com.example.myapplication.loginScreens.login_screen

class ForgotPasswordScreen : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.forgotPasswordBackArrow.setOnClickListener(View.OnClickListener {
            navigateToActivity(login_screen())
        })

        binding.resetPasswordBtn.setOnClickListener(View.OnClickListener {
            val emailVal = binding.emailETFieldforgotPassword.text.toString().trim()

            if (isValidEmail(emailVal)) {
                binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_background)
                binding.emailErrorMsgforgotPassword.visibility = View.GONE
                binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    null,
                    null
                )
                navigateToActivity(ResetPasswordScreen())
            } else {
                binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_error_background)
                binding.emailErrorMsgforgotPassword.text = "Invalid email format"
                binding.emailErrorMsgforgotPassword.visibility = View.VISIBLE
                binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null,
                    null,
                    resources.getDrawable(R.drawable.alert_circle),
                    null
                )
            }
        })

        binding.emailETFieldforgotPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString().trim()
                if (email.isNotEmpty()) {
                    if (!isValidEmail(email)) {
                        // Invalid email format
                        binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_error_background)
                        binding.emailErrorMsgforgotPassword.text = "Invalid email format"
                        binding.emailErrorMsgforgotPassword.visibility = View.VISIBLE
                        binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            resources.getDrawable(R.drawable.alert_circle),
                            null
                        )
                    } else {
                        // Valid email format
                        binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_background)
                        binding.emailErrorMsgforgotPassword.visibility = View.GONE
                        binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            null,
                            null,
                            null,
                            null
                        )
                    }
                } else {
                    binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_background)
                    binding.emailErrorMsgforgotPassword.visibility = View.GONE
                    binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        null,
                        null
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    // Method for checking email
    fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun navigateToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
        finish()
    }
}


//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.view.View
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.example.myapplication.R
//import com.example.myapplication.databinding.ActivityForgotPasswordScreenBinding
//import com.example.myapplication.loginScreens.login_screen
//
//class ForgotPasswordScreen : AppCompatActivity() {
//
//    private lateinit var binding: ActivityForgotPasswordScreenBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityForgotPasswordScreenBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val storedEmail = "khushbu@indicchain.com"
//
//        binding.emailETFieldforgotPassword.setOnClickListener(View.OnClickListener {
//            Toast.makeText(this, "Dummy email is: $storedEmail", Toast.LENGTH_LONG).show()
//        })
//
//        binding.forgotPasswordBackArrow.setOnClickListener(View.OnClickListener {
//            navigateToActivity(login_screen())
//        })
//
//
//
//        binding.resetPasswordBtn.setOnClickListener(View.OnClickListener {
//            var emailVal = binding.emailETFieldforgotPassword.text.toString().trim()
//            if (emailVal == storedEmail){
//                binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_background)
//                binding.emailErrorMsgforgotPassword.visibility = View.GONE
//                binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
//                navigateToActivity(ResetPasswordScreen())
//            }else{
//                binding.emailETFieldforgotPassword.setBackgroundResource(R.drawable.edittext_error_background)
//                binding.emailErrorMsgforgotPassword.text = "Email does not exist"
//                binding.emailErrorMsgforgotPassword.visibility = View.VISIBLE
//                binding.emailETFieldforgotPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.alert_circle), null)
//            }
//        })
//
//    }
//
//    private fun navigateToActivity(activity: Activity) {
//        val intent = Intent(this, activity::class.java)
//        startActivity(intent)
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
////        sharedPreferences.edit().putBoolean("isOnboardingShown", true).apply()
//        finish()
//    }
//}