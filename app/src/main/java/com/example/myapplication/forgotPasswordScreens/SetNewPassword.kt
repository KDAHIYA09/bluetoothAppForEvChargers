package com.example.myapplication.forgotPasswordScreens

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.R
import com.example.myapplication.authScreen.DrawableClickListener
import com.example.myapplication.databinding.ActivitySetNewPasswordBinding
import com.example.myapplication.loginScreens.login_screen

class SetNewPassword : AppCompatActivity() {
    private lateinit var binding: ActivitySetNewPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySetNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Toast.makeText(this, "New password: Mobiloitte!1", Toast.LENGTH_SHORT).show()

        binding.passwordETFieldnewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var pass = binding.passwordETFieldnewPassword.text.toString().trim()
                if (!isPasswordValid(pass)){
                    binding.passwordETFieldnewPassword.setBackgroundResource(R.drawable.edittext_error_background)
                    binding.PsswordErrorMsgNEWPASS.text = "Password must be 8-16 characters long with 1 uppercase, 1 lowercase, 1 number and 1 special character."
                    binding.PsswordErrorMsgNEWPASS.visibility = View.VISIBLE
                }else{
                    binding.passwordETFieldnewPassword.setBackgroundResource(R.drawable.edittext_background)
                    binding.PsswordErrorMsgNEWPASS.visibility = View.GONE
                }

            }

            override fun afterTextChanged(s: Editable?) {
                var pass = binding.passwordETFieldnewPassword.text.toString().trim()
                if (!isPasswordValid(pass)){
                    binding.passwordETFieldnewPassword.setBackgroundResource(R.drawable.edittext_error_background)
                    binding.PsswordErrorMsgNEWPASS.text = "Password must be 8-16 characters long with 1 uppercase, 1 lowercase, 1 number and 1 special character."
                    binding.PsswordErrorMsgNEWPASS.visibility = View.VISIBLE
                }else{
                    binding.passwordETFieldnewPassword.setBackgroundResource(R.drawable.edittext_background)
                    binding.PsswordErrorMsgNEWPASS.visibility = View.GONE
                }
            }

        })

        binding.repeatPasswordETFieldNEWPASS.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                var pass = binding.passwordETFieldnewPassword.text.toString().trim()
                var confirmPass = binding.repeatPasswordETFieldNEWPASS.text.toString().trim()
                if (confirmPass != pass){
                    binding.repeatPasswordETFieldNEWPASS.setBackgroundResource(R.drawable.edittext_error_background)
                    binding.confirmPasswordErrorMsgNEWPASS.text = "Password and confirm password do not match"
                    binding.confirmPasswordErrorMsgNEWPASS.visibility = View.VISIBLE
                }else{
                    binding.repeatPasswordETFieldNEWPASS.setBackgroundResource(R.drawable.edittext_background)
                    binding.confirmPasswordErrorMsgNEWPASS.text = "Password and confirm password do not match"
                    binding.confirmPasswordErrorMsgNEWPASS.visibility = View.GONE
                }
            }

        })

        var isIcon1Visible = true

        binding.passwordETFieldnewPassword.setDrawableClickListener(object : DrawableClickListener {
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                if (target == DrawableClickListener.DrawablePosition.END) {
                    if (isIcon1Visible) {
                        binding.passwordETFieldnewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.eye_open), null)
                        binding.passwordETFieldnewPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        binding.passwordETFieldnewPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.eye_off), null)
                        binding.passwordETFieldnewPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    isIcon1Visible = !isIcon1Visible // Toggle the flag
                }
            }
        })

        binding.repeatPasswordETFieldNEWPASS.setDrawableClickListener(object : DrawableClickListener {
            override fun onClick(target: DrawableClickListener.DrawablePosition) {
                if (target == DrawableClickListener.DrawablePosition.END) {
                    if (isIcon1Visible) {
                        binding.repeatPasswordETFieldNEWPASS.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.eye_open), null)
                        binding.repeatPasswordETFieldNEWPASS.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    } else {
                        binding.repeatPasswordETFieldNEWPASS.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.eye_off), null)
                        binding.repeatPasswordETFieldNEWPASS.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                    }
                    isIcon1Visible = !isIcon1Visible // Toggle the flag
                }
            }
        })

        binding.resetpasswordBtnNEWPASS.setOnClickListener(View.OnClickListener {
            var pass = binding.passwordETFieldnewPassword.text.toString().trim()
            var confirmPass = binding.repeatPasswordETFieldNEWPASS.text.toString().trim()

            if(isPasswordValid(pass) && pass == confirmPass){
                Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()

                navigateToActivity(login_screen())
            }else{
                Toast.makeText(this, "Please enter all the values correctly.", Toast.LENGTH_SHORT).show()
            }

        })

    }

    fun isPasswordValid(password: String): Boolean {
        // Define regex patterns for each criteria
        val uppercasePattern = Regex("[A-Z]")
        val lowercasePattern = Regex("[a-z]")
        val digitPattern = Regex("\\d")
        val specialCharPattern = Regex("[^A-Za-z0-9]")

        // Check if password meets all criteria
        val hasUppercase = uppercasePattern.containsMatchIn(password)
        val hasLowercase = lowercasePattern.containsMatchIn(password)
        val hasDigit = digitPattern.containsMatchIn(password)
        val hasSpecialChar = specialCharPattern.containsMatchIn(password)
        val isLengthValid = password.length in 8..16

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar && isLengthValid
    }

    private fun navigateToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        sharedPreferences.edit().putBoolean("isOnboardingShown", true).apply()
        finish()
    }
}