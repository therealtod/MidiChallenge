package com.example.midichallenge.models

import android.content.Context
import com.example.midichallenge.utils.QuestionManager

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