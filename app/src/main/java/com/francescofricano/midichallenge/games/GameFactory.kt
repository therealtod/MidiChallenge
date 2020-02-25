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
                sID = it.song.id,
                suggestion = it.suggestion,
                answer = it.answer
            )
        }
        return SoloGame(
            soloGameQuestions.toMutableList(),
            context
        )
    }
    /*
    fun makeMultiplayerGameFromFirebaseDocument(document: DocumentSnapshot) : MultiplayerGame{
        Log.i(LOG_TAG, "Creating a game")
        val players = document.get("players") as List<*>
        val p = players.map {
            it as String
        }
        val playerOnTurnIndex = document.get("playerOnTurnIndex") as Long
        val questionsFromDatabase = document.get("questions") as List<*>
        val gameQuestions = mutableListOf<ClassicGameQuestion>()
        for (item in questionsFromDatabase) {
            if (item is HashMap<*,*>) {
                val map = item as HashMap<String, Any>
                val songMap = map.get("song") as HashMap<String, Any>
                val song = Song.fromHashMap(songMap)
                gameQuestions.add(ClassicGameQuestion(
                    sID = song.id,
                    suggestion = map.get("suggestion") as String,
                    answer = map.get("answer") as String
                ))
            }
        }


        return MultiplayerGame(gameQuestions, context, p, playerOnTurnIndex.toString().toInt(), document.reference)
    }

     */

    fun makeMultiplayerGameFromFirebaseDocument(document: DocumentSnapshot) : MultiplayerGame{
        val gameFromDatabase = document.toObject(com.francescofricano.midichallenge.models.database.MultiplayerGame::class.java)
        val questions = gameFromDatabase!!.questions.map{
            ClassicGameQuestion(
                it.song!!.id,
                it.suggestion,
                it.answer,
                0
            )
        }
        return MultiplayerGame(
            questions.toMutableList(),
            context, gameFromDatabase.players,
            gameFromDatabase.playerOnTurnIndex,
            document.reference)
    }
}
