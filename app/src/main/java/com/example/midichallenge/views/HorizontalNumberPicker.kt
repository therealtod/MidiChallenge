package com.example.midichallenge.views

import android.widget.EditText
import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.Nullable
import com.example.midichallenge.R

class HorizontalNumberPicker(context: Context, @Nullable attrs: AttributeSet) :
    LinearLayout(context, attrs) {
    private val etNumber: EditText?
    var min: Int = 0
    var max: Int = 0
    private val btnLess: Button
    private val btnMore: Button

    /** GETTERS & SETTERS */

    var value: Int
        get() {
            if (etNumber != null) {
                try {
                    val value = etNumber.text.toString()
                    return Integer.parseInt(value)
                } catch (ex: NumberFormatException) {
                    Log.e("HorizontalNumberPicker", ex.toString())
                }
            }
            return 0
        }
        set(value) {
            etNumber?.setText(value.toString())
        }

    init {
        View.inflate(context, R.layout.horizontal_number_picker, this)
        etNumber = findViewById(R.id.et_number)
        btnLess = findViewById(R.id.btn_less)
        btnLess.setOnClickListener(AddHandler(-1))
        btnMore = findViewById(R.id.btn_more)
        btnMore.setOnClickListener(AddHandler(1))
    }

    /** HANDLERS */

    private inner class AddHandler(internal val diff: Int) : OnClickListener {

        override fun onClick(v: View) {
            var newValue = value + diff
            if (newValue < min) {
                newValue = min
            } else if (newValue > max) {
                newValue = max
            }
            etNumber!!.setText(newValue.toString())
        }
    }

    fun freezeButtons() {
        btnLess.isEnabled = false
        btnMore.isEnabled = false
    }

    fun unfreezeButtons() {
        btnLess.isEnabled = true
        btnMore.isEnabled = true
    }
}