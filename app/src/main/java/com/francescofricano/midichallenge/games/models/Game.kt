package com.francescofricano.midichallenge.games.models

import com.francescofricano.midichallenge.utils.FirebaseWritable

abstract class Game (val q: MutableList<out GameQuestion>): FirebaseWritable{
    var currentQuestionNumber = 1
    abstract val score: Int

    open fun getCurrentQuestion() : GameQuestion{
        return q[currentQuestionNumber - 1]
    }

    val numberOfQuestions: Int
        get() {
            return q.size
        }

    fun goToNextQuestion() {
        currentQuestionNumber ++
    }

    fun isOver() : Boolean{
        return currentQuestionNumber > q.size
    }

    abstract fun answerCurrentQuestion(playerAnswer: String, notesUsed: Int) : Boolean

    override fun toHashMap() {
        return
    }

    companion object {
        abstract class GameStatus
    }

}