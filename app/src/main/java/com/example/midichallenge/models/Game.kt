package com.example.midichallenge.models

import android.content.Context
import com.example.midichallenge.R
import com.example.midichallenge.utils.QuestionManager

class ClassicGame (val context: Context){
    val questions: List<ClassicGameQuestion>
    val questionManager = QuestionManager(context)
    init {
        val q = questionManager.getQuestions(context.resources.getInteger(R.integer.number_of_questions_classic_game_mode))
        questions = q.map{ClassicGameQuestion(
            0, it.suggestion, it.answer
        )}
    }

}