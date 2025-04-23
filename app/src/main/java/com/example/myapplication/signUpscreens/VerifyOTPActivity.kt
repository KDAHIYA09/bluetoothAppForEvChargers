package com.example.myapplication.authScreen
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivityVerifyOtpactivityBinding
import com.example.myapplication.loginScreens.login_screen

class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpactivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var dummyOtp = 100000
        Toast.makeText(this, "dummyOtp is : $dummyOtp", Toast.LENGTH_LONG).show()
        //create an array of all text field of otp digits
        val otpDigits = arrayOfNulls<EditText>(6)
        otpDigits[0] = binding.otp1
        otpDigits[1] = binding.otp2
        otpDigits[2] = binding.otp3
        otpDigits[3] = binding.otp4
        otpDigits[4] = binding.otp5
        otpDigits[5] = binding.otp6

        //add listener to all fields/array element
        for (i in 0..5) {
            otpDigits[i]?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    if (s?.length == 1) {
                        // Move to the next field if a digit is entered
                        if (i < 5) {
                            otpDigits[i + 1]?.requestFocus()
                        }
                    } else if (s?.length == 0) {
                        //move to previous field if backspace is pressed.
                        if (i > 0) {
                            otpDigits[i - 1]?.requestFocus()
                        }
                    }
                }
            })

            //add backspace listener for deleting a digit.
            otpDigits[i]?.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    // Handle backspace key press
                    if (i > 0 && otpDigits[i]?.text.isNullOrEmpty()) {
                        otpDigits[i - 1]?.requestFocus()
                        otpDigits[i - 1]?.setText("")
                        return@OnKeyListener true

                    }
                }
                false
            })
        }

        binding.submitOtpBtn.setOnClickListener(View.OnClickListener {
            val otp = StringBuilder()
            for (i in 0..5) {
                otp.append(otpDigits[i]?.text)
            }
            //Toast.makeText(this , "Otp is : $otp", Toast.LENGTH_LONG).show()

            if (otp.toString() != dummyOtp.toString()) {
                setSuccessMessage()
            } else if (otpDigits.any { it?.text.toString().isEmpty() }) {
                setErrorMessage()
                binding.OTPErrorMsg.text = "Please enter OTP"
                binding.OTPErrorMsg.visibility = View.VISIBLE
            } else {
                setErrorMessage()
                binding.OTPErrorMsg.text = "Wrong code"
                binding.OTPErrorMsg.visibility = View.VISIBLE
            }

        })

        binding.resendCodeText.setOnClickListener(View.OnClickListener {
            otpDigits.forEach { editText ->
                editText?.setText("")
            }

            otpDigits[0]?.requestFocus()

        })
    }


    private fun navigateToActivity(activity: Activity) {
        val intent = Intent(this, activity::class.java)
        startActivity(intent)
//        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
//        sharedPreferences.edit().putBoolean("isOnboardingShown", true).apply()
        finish()
    }

    private fun setSuccessMessage() {
        binding.OTPErrorMsg.visibility = View.GONE
        binding.otp1.setBackgroundResource(R.drawable.edittext_background)
        binding.otp2.setBackgroundResource(R.drawable.edittext_background)
        binding.otp3.setBackgroundResource(R.drawable.edittext_background)
        binding.otp4.setBackgroundResource(R.drawable.edittext_background)
        binding.otp5.setBackgroundResource(R.drawable.edittext_background)
        binding.otp6.setBackgroundResource(R.drawable.edittext_background)
        navigateToActivity(login_screen())
    }

    private fun setErrorMessage() {
        binding.otp1.setBackgroundResource(R.drawable.edittext_error_background)
        binding.otp2.setBackgroundResource(R.drawable.edittext_error_background)
        binding.otp3.setBackgroundResource(R.drawable.edittext_error_background)
        binding.otp4.setBackgroundResource(R.drawable.edittext_error_background)
        binding.otp5.setBackgroundResource(R.drawable.edittext_error_background)
        binding.otp6.setBackgroundResource(R.drawable.edittext_error_background)

    }

}


//
//
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.view.View
//import android.widget.EditText
//import android.widget.Toast
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import androidx.core.view.isVisible
//import com.example.myapplication.R
//import com.example.myapplication.databinding.ActivityVerifyOtpactivityBinding
//import com.example.myapplication.loginScreens.login_screen
//
//class VerifyOTPActivity : AppCompatActivity() {
//
//    private lateinit var binding : ActivityVerifyOtpactivityBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        var dummyOtp = 100000
//        Toast.makeText(this , "dummyOtp is : $dummyOtp", Toast.LENGTH_LONG).show()
//        //create an array of all text field of otp digits
//        val otpDigits = arrayOfNulls<EditText>(6)
//        otpDigits[0] = binding.otp1
//        otpDigits[1] = binding.otp2
//        otpDigits[2] = binding.otp3
//        otpDigits[3] = binding.otp4
//        otpDigits[4] = binding.otp5
//        otpDigits[5] = binding.otp6
//
//        //add listner to all fields/array element to make sure cursour moves to next field after 1 digit has been typed in
//        for (i in 0..4){
//            otpDigits[i]?.addTextChangedListener(object : TextWatcher{
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    if (s?.length == 1){
//                        otpDigits[i+1]?.requestFocus()
//                    }
//                }
//
//
//            })
//
//            //for last otp digit we want that focus moves handling submission event
//            otpDigits[5]?.addTextChangedListener(object : TextWatcher{
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//
//                }
//
//            })
//
//
//        }
//
//        binding.submitOtpBtn.setOnClickListener(View.OnClickListener {
//            val otp = StringBuilder()
//            for (i in 0..5){
//                otp.append(otpDigits[i]?.text)
//            }
//            //Toast.makeText(this , "Otp is : $otp", Toast.LENGTH_LONG).show()
//
//            if (otp.toString() == dummyOtp.toString()){
//                setSuccessMessage()
//            }else if(otpDigits.any { it?.text.toString().isEmpty() }){
//                setErrorMessage()
//                binding.OTPErrorMsg.text = "Please enter OTP"
//                binding.OTPErrorMsg.visibility = View.VISIBLE
//            }else{
//                setErrorMessage()
//                binding.OTPErrorMsg.text = "Wrong code"
//                binding.OTPErrorMsg.visibility = View.VISIBLE
//            }
//
//        })
//
//        binding.resendCodeText.setOnClickListener(View.OnClickListener {
//                otpDigits.forEach { editText ->
//                    editText?.setText("")
//                }
//
//                otpDigits[0]?.requestFocus()
//
//        })
//
//
//
//
//    }
//
//
//    private fun navigateToActivity(activity: Activity) {
//        val intent = Intent(this, activity::class.java)
//        startActivity(intent)
////        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
////        sharedPreferences.edit().putBoolean("isOnboardingShown", true).apply()
//        finish()
//    }
//
//    private fun setSuccessMessage(){
//        binding.OTPErrorMsg.visibility = View.GONE
//        binding.otp1.setBackgroundResource(R.drawable.edittext_background)
//        binding.otp2.setBackgroundResource(R.drawable.edittext_background)
//        binding.otp3.setBackgroundResource(R.drawable.edittext_background)
//        binding.otp4.setBackgroundResource(R.drawable.edittext_background)
//        binding.otp5.setBackgroundResource(R.drawable.edittext_background)
//        binding.otp6.setBackgroundResource(R.drawable.edittext_background)
//        navigateToActivity(login_screen())
//    }
//
//    private fun setErrorMessage(){
//        binding.otp1.setBackgroundResource(R.drawable.edittext_error_background)
//        binding.otp2.setBackgroundResource(R.drawable.edittext_error_background)
//        binding.otp3.setBackgroundResource(R.drawable.edittext_error_background)
//        binding.otp4.setBackgroundResource(R.drawable.edittext_error_background)
//        binding.otp5.setBackgroundResource(R.drawable.edittext_error_background)
//        binding.otp6.setBackgroundResource(R.drawable.edittext_error_background)
//
//    }
//
//}