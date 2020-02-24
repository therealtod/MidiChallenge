package com.francescofricano.midichallenge.games.models

import android.content.Context
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.IgnoreExtraProperties


class MultiplayerGame (questions: MutableList<ClassicGameQuestion>,
                       context: Context,
                       p: List<String>,
                       private var playerOnTurnIndex: Int,
                       private val documentReference: DocumentReference) : SoloGame(questions, context) {

    private val players: List<Player> = p.map { Player(it) }
    var currentBid = 10
    private val changeListeners=  mutableListOf<() -> Unit>()
    private val rounds: List<Round>
    private var currentRound = 0

    init {
        rounds = questions.map {
            Round(it, players.toMutableList())
        }
    }

    val playerOnTurn: Player
        get() {
            return players[playerOnTurnIndex]
        }

    private fun switchToNextPlayer() {
        playerOnTurnIndex = (playerOnTurnIndex +1) % players.size
    }

    fun playerOnTurnPasses() {
        switchToNextPlayer()
        documentReference.update(
            mapOf(
                "playerOTurnIndex" to playerOnTurnIndex
            )
        )
    }

    fun playerOnTurnBids(notes: Int) {
        if (currentBid == 1) {
            throw GameIsClosedException()
        }
        if (notes == 1) {
            //TODO
            return
        }
        if (notes < currentBid) {
            currentBid= notes
            switchToNextPlayer()
            documentReference.update(
                mapOf(
                    "currentNotes" to notes
                )
            )
            return
        }
    }

    fun isPlayersTurn(playerID: String) : Boolean {
        return playerID == playerOnTurn.id
    }

    fun addOnChangeListener(onChangeListener: () -> Unit) {
       this.changeListeners.add(onChangeListener)
    }

    fun updateFromDatabaseData(dataMap: MutableMap<String, Any>?) {
    }
}


/*
class MultiplayerGame(private val documentReference: DocumentReference)
*/

class GameIsClosedException (message: String = "The round is already closed") : Exception(message)