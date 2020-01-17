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

class PlayerScore(context: Context, @Nullable attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val playerScore : TextView?
    private val image: ImageView

    init {
        View.inflate(context, R.layout.player_score, this)
        playerScore = findViewById(R.id.textViewPlayerScore)
        image = findViewById(R.id.imageViewPlayerScore)
    }

    var score: Int
        get() {
            if (playerScore != null) {
                try {
                    val value = playerScore.text.toString()
                    return Integer.parseInt(value)
                } catch (ex: NumberFormatException) {
                    Log.e("HorizontalNumberPicker", ex.toString())
                }
            }
            return 0
        }
        set(value) {
            playerScore?.setText(value.toString())
        }
}
