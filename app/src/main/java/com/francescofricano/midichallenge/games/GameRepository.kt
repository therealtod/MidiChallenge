package com.francescofricano.midichallenge.games

import android.content.Context
import android.util.Log
import com.francescofricano.midichallenge.games.models.Game
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.games.models.SoloGame
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

object GameRepository {
    private val soloGames: MutableMap<String, Game> = mutableMapOf()
    private val multiplayerGames: MutableMap<String, MultiplayerGame> = mutableMapOf()
    private lateinit var context: Context
    private val LOG_TAG = this.javaClass.name

    fun getGame(id: String) : Game {
        return soloGames[id]
            ?: throw GameNotFoundException("Game with id $id not found in the game repository")
    }

    fun getSoloGame(id: String): SoloGame {
        val game = soloGames[id]
        if (game != null && game is SoloGame) {
            return game
        }
        else {
            throw GameNotFoundException("The game is not of type SoloGame")
        }
    }

    fun getMultiplayerGame(id: String): MultiplayerGame {
        val game = multiplayerGames[id]
        if (game != null) {
            return game
        }
        else {
            throw GameNotFoundException("The game is not of type MultiplayerGame")
        }
    }

    fun setContext (context: Context) : GameRepository {
        this.context = context
        return this
    }

    fun newSoloGame() : String {
        val game = GameFactory.setContext(context).makeNewSoloGame()
        val id = UUID.randomUUID().toString()
        soloGames[id] = game
        return id
    }

    fun newMultiplayerGame(doc: DocumentSnapshot): String {
        Log.i(LOG_TAG, "Asking GameFactory to create a game")
        val game = GameFactory
            .setContext(context).makeMultiplayerGameFromFirebaseDocument(doc)
        val id = UUID.randomUUID().toString()
        Log.i(LOG_TAG, "Assigned this id: $id to the new game")
        multiplayerGames[id] = game
        return id
    }
}

class GameNotFoundException(message: String) :Exception(message)