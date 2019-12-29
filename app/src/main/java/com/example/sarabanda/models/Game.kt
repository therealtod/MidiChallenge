package com.example.sarabanda.models

import android.content.Context
import com.example.sarabanda.R
import com.example.sarabanda.utils.QuestionManager

abstract class Game{
    abstract var questions: ArrayList<Question>

}

class ClassicGame (val context: Context){
    val questions: List<ClassicGameQuestion>
    val questionManager = QuestionManager(context)
    init {
        val q = questionManager.getQuestions(3)
        questions = q.map{ClassicGameQuestion(
            0, it.suggestion, it.answer
        )}
    }

}