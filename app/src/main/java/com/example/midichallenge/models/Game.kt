package com.example.midichallenge.models

import android.content.Context
import com.example.midichallenge.R
import com.example.midichallenge.utils.QuestionManager
import java.lang.Error

class ClassicGame (val context: Context){
    val questions: List<ClassicGameQuestion>
    private val questionManager = QuestionManager(context)
    init {
        val q = questionManager.getQuestions(context.resources.getInteger(R.integer.number_of_questions_classic_game_mode))
        questions = q.map{ClassicGameQuestion(
            0, it.suggestion, it.answer
        )}
    }

    fun getScore() : Int{
        var tot = 0
        for (question in questions) {
            tot += question.points
        }
        return tot
        //return questions.reduce{a, b -> a.points + b.points}
    }

    fun answerQuestion(notesUsed: Int, questionNumber: Int) {
        val max = context.resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        if (notesUsed in 0..max) {
            questions[questionNumber].points = max - notesUsed
        }
        else {
            throw Error("Value passed not in correct range")
        }
    }

}
