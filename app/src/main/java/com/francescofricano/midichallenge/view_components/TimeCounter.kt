package com.francescofricano.midichallenge.view_components

import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import com.francescofricano.midichallenge.R

class TimeCounter(context: Context, @Nullable attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val timeCounter : TextView?
    private val image: ImageView

    init {
        View.inflate(context, R.layout.time_counter, this)
        timeCounter = findViewById(R.id.timeTextTimeCounter)
        image = findViewById(R.id.imageViewTimeCounter)
    }


    fun setTime(timeString: String) {
        timeCounter!!.text = timeString
    }
    
}
