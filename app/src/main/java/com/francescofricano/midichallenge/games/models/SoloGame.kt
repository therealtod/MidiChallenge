package com.francescofricano.midichallenge.games.models

import android.content.Context
import com.francescofricano.midichallenge.R

open class SoloGame (
    private val questions: MutableList<ClassicGameQuestion>,
    val context: Context
)
    : Game(questions) {
    private val maxPoints = context.resources
        .getInteger(R.integer.max_number_notes_classic_game_mode) + 1

    override val score: Int
        get() {
            return questions.fold(0){sum, x -> sum + x.points}
        }

    override fun answerCurrentQuestion(playerAnswer: String, notesUsed: Int) : Boolean{
        val points = maxPoints - notesUsed
        val question = questions[currentQuestionNumber - 1]
        return question.answerQuestion(playerAnswer, points)
    }

    override fun getCurrentQuestion () :ClassicGameQuestion {
        return questions[currentQuestionNumber - 1]
    }


}