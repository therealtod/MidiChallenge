package com.francescofricano.midichallenge.games.models

import java.util.*

abstract class GameQuestion(val ans: Any)

open class ClassicGameQuestion(val sID: Int,
                               val suggestion: String,
                               val answer: String,
                               var points: Int = 0) : GameQuestion(answer) {

    fun answerQuestion(answer: String, points: Int) : Boolean{
        if (answer.toLowerCase(Locale.ITALY).trim() == this.answer.toLowerCase(Locale.ITALY).trim()) {
            this.points = points
            return true
        }
        return false
    }
}


class MultiplayerGameQuestion(sID: Int,
                                  suggestion: String,
                                  answer: String,
                                  points: Int = 0,
                                  var open: Boolean = true
                                  ) : ClassicGameQuestion(sID, suggestion, answer, points)
