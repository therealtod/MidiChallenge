package com.francescofricano.midichallenge.games

import android.content.Context
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.models.SoloGame
import com.francescofricano.midichallenge.games.models.ClassicGameQuestion
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.utils.QuestionManager
import com.google.firebase.firestore.DocumentSnapshot

object GameFactory {
    private lateinit var context: Context
    private val LOG_TAG = this.javaClass.name

    fun setContext(context: Context) : GameFactory {
        GameFactory.context = context
        return this
    }
    fun makeNewSoloGame(): SoloGame {
        val numberOfQuestions = context.resources
            .getInteger(R.integer.number_of_questions_classic_game_mode)
        val questions = QuestionManager.setContext(context).getNQuestions(numberOfQuestions)
        val soloGameQuestions = questions.map {
            ClassicGameQuestion(
                sID = it.song.sid,
                suggestion = it.suggestion,
                answer = it.answer
            )
        }
        return SoloGame(
            soloGameQuestions.toMutableList(),
            context
        )
    }

    fun makeMultiplayerGameFromFirebaseDocument(document: DocumentSnapshot) : MultiplayerGame{
        return MultiplayerGame(document, document.reference)
    }
}
