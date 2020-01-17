package com.example.midichallenge.view_components

import android.widget.LinearLayout
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.Nullable
import com.example.midichallenge.R

class QuestionCounter(context: Context, @Nullable attrs: AttributeSet) : LinearLayout(context, attrs) {
    private val questionCounter : TextView?
    private val maxQuestionsNumber : TextView?
    init {
        View.inflate(context, R.layout.question_counter, this)
        questionCounter = findViewById(R.id.textViewQuestionCounter)
        maxQuestionsNumber = findViewById(R.id.textViewMaxQuestionsNumber)
    }

    var currentQuestionNumber: Int
        get() {
            if (questionCounter != null) {
                try {
                    val value = questionCounter.text.toString()
                    return Integer.parseInt(value)
                } catch (ex: NumberFormatException) {
                    Log.e("HorizontalNumberPicker", ex.toString())
                }
            }
            return 0
        }
        set(currentQuestionNumber) {
            questionCounter?.setText(currentQuestionNumber.toString())
        }

    fun incrementCurrentQuestion() {
        val max = this.resources.getInteger(R.integer.number_of_questions_classic_game_mode)
        if(currentQuestionNumber < max) {
            currentQuestionNumber += 1
        } else {
            throw Exception ("Current value reached maximum")
        }
    }

}

