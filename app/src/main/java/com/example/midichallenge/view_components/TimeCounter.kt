package com.example.midichallenge.view_components

import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.example.midichallenge.R

class TimeCounter(context: Context, @Nullable attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val timeCounter : TextView?
    private val image: ImageView

    init {
        View.inflate(context, R.layout.time_counter, this)
        timeCounter = findViewById(R.id.timeTextTimeCounter)
        image = findViewById(R.id.imageViewTimeCounter)
    }

    var time: Int
        get() {
            if (timeCounter != null) {
                try {
                    val value = timeCounter.text.toString()
                    return Integer.parseInt(value)
                } catch (ex: NumberFormatException) {
                    Log.e("HorizontalNumberPicker", ex.toString())
                }
            }
            return 0
        }
        set(value) {
            timeCounter?.setText(value.toString())
        }
    
}
