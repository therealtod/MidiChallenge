package com.example.midichallenge.models

import android.content.Context
import com.example.midichallenge.R
import com.example.midichallenge.utils.QuestionManager
import java.lang.Error

class ClassicGame (val context: Context){
    val questions: List<ClassicGameQuestion>
    init {
        val q = QuestionManager.getQuestions(context.resources.getInteger(R.integer.number_of_questions_classic_game_mode))
        questions = q.map{ClassicGameQuestion(
            0, it.suggestion, it.answer
        )}
    }

    fun getScore() : Int{
        return questions.fold(0){sum, x -> sum + x.points}
    }

    fun answerQuestion(notesUsed: Int, questionNumber: Int, isPlayed : Boolean) {
        val max = context.resources.getInteger(R.integer.max_number_notes_classic_game_mode) + 1
        if(isPlayed) {
            if (notesUsed in 0..max) {
                questions[questionNumber].points = max - notesUsed
            } else {
                throw Error("Value passed not in correct range")
            }
        } else {
            questions[questionNumber].points = max
        }
    }

}
