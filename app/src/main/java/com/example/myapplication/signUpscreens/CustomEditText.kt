package com.example.myapplication.authScreen

import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText : AppCompatEditText {
    private var drawableClickListener: DrawableClickListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setDrawableClickListener(listener: DrawableClickListener) {
        this.drawableClickListener = listener
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && compoundDrawables[2] != null) {
            val touchArea = Rect()
            getGlobalVisibleRect(touchArea)
            touchArea.left = touchArea.right - compoundDrawables[2].bounds.width() // Adjust touch area for the drawable
            if (touchArea.contains(event.rawX.toInt(), event.rawY.toInt())) {
                drawableClickListener?.onClick(DrawableClickListener.DrawablePosition.END)
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    init {
        // Add a TextWatcher to monitor text changes
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                // Remove whitespace characters
                s?.let {
                    val newText = it.toString().replace("\\s".toRegex(), "")
                    if (it.toString() != newText) {
                        setText(newText)
                        setSelection(newText.length) // Place cursor at the end
                    }
                }
            }
        })
    }

}


