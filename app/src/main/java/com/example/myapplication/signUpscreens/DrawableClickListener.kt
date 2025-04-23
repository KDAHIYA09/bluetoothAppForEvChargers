package com.example.myapplication.authScreen

interface DrawableClickListener {
    enum class DrawablePosition { TOP, BOTTOM, START, END }
    fun onClick(target: DrawablePosition)
}


//log in
//sign in
//otp
//forgot pass
//iska otp same screen-->>>>
