package com.francescofricano.midichallenge.games.models

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot



class MultiplayerGame(private val initialSnapshot: DocumentSnapshot,
                      private val documentReference: DocumentReference) {
    private val playerOnTurnIndexFieldName = "playerOnTurnIndex"
    private val LOG_TAG = this.javaClass.name
    private val questions = mutableListOf<MultiplayerGameQuestion>()
    var players = mutableListOf<Player>()
    var playerOnTurnIndex = 0
    var status = "waiting"
    var currentBid = 10
    var listeners = mutableListOf<() -> Unit>()
    var currentQuestionIndex = 0

    init {
        updateFromSnapshot(initialSnapshot)
        documentReference
            .addSnapshotListener{ snapshot, err ->
                if (err != null) {
                    Log.w(LOG_TAG, "Listen failed.", err)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val data = snapshot.data
                    Log.d(LOG_TAG, "Current data for this game: $data")
                    updateFromSnapshot(snapshot)
                    for (l in listeners) {
                        l()
                    }

                } else {
                    Log.d(LOG_TAG, "Current data: null")
                }
            }
    }


    val currentQuestion: MultiplayerGameQuestion
    get() {
        return questions[currentQuestionIndex]
    }

    private fun updateFromSnapshot(snapshot : DocumentSnapshot) {
        val gameFromDatabase = snapshot
            .toObject(com.francescofricano.midichallenge.models.database.MultiplayerGame::class.java)
        Log.i(LOG_TAG, "Adding questions")
        questions.addAll(gameFromDatabase!!.questions.map{
            MultiplayerGameQuestion(
                it.song!!.id,
                it.suggestion,
                it.answer,
                0
            )
        })
        Log.i(LOG_TAG, "Adding players")
        players.addAll(gameFromDatabase.players.map { Player(it) })
        playerOnTurnIndex = gameFromDatabase.playerOnTurnIndex
        status = gameFromDatabase.status
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
        currentQuestion.open = false
        documentReference.update(
            mapOf(
                playerOnTurnIndexFieldName to playerOnTurnIndex
            )
        )
    }

    fun playerOnTurnBids(notes: Int) {
        if (currentBid == 1) {
            throw GameIsClosedException()
        }
        if (notes == 1) {
            currentQuestion.open = false
            documentReference.update(
                mapOf(
                    "currentNotes" to notes,
                    playerOnTurnIndexFieldName to playerOnTurnIndex
                )
            )
            return
        }
        if (notes < currentBid) {
            currentBid= notes
            switchToNextPlayer()
            documentReference.update(
                mapOf(
                    "currentNotes" to notes,
                    playerOnTurnIndexFieldName to playerOnTurnIndex
                )
            )
            return
        }
    }

    fun isPlayersTurn(playerID: String) : Boolean {
        return playerID == playerOnTurn.id
    }

    fun addListener(listener: ()-> Unit) {
        listeners.add(listener)
    }

}


class GameIsClosedException (message: String = "The round is already closed") : Exception(message)
