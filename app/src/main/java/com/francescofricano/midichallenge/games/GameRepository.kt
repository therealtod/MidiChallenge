package com.francescofricano.midichallenge.games

import android.content.Context
import com.francescofricano.midichallenge.games.models.Game
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.games.models.SoloGame
import com.google.firebase.firestore.DocumentSnapshot
import java.util.*

object GameRepository {
    private val games: MutableMap<String, Game> = mutableMapOf()
    private lateinit var context: Context

    fun getGame(id: String) : Game {
        return games[id]
            ?: throw GameNotFoundException("Game with id $id not found in the game repository")
    }

    fun getSoloGame(id: String): SoloGame {
        val game = games[id]
        if (game != null && game is SoloGame) {
            return game
        }
        else {
            throw GameNotFoundException("The game is not of type SoloGame")
        }
    }

    fun getMultiplayerGame(id: String): MultiplayerGame {
        val game = games[id]
        if (game != null && game is MultiplayerGame) {
            return game
        }
        else {
            throw GameNotFoundException("The game is not of type SoloGame")
        }
    }

    fun setContext (context: Context) : GameRepository {
        this.context = context
        return this
    }

    fun newSoloGame() : String {
        val game = GameFactory.setContext(context).makeNewSoloGame()
        val id = UUID.randomUUID().toString()
        games[id] = game
        return id
    }

    fun newMultiplayerGame(doc: DocumentSnapshot): String {
        val game = GameFactory
            .setContext(context).makeMultiplayerGameFromFirebaseDocument(doc)
        val id = UUID.randomUUID().toString()
        games[id] = game
        return id
    }
}

class GameNotFoundException(message: String) :Exception(message)