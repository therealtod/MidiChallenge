package com.francescofricano.midichallenge.games.models

import android.content.Context
import android.util.Log
import com.francescofricano.midichallenge.models.database.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.NullPointerException


class MultiplayerGame(private val initialSnapshot: DocumentSnapshot,
                      private val documentReference: DocumentReference) {
    private val myUid = FirebaseAuth.getInstance().currentUser!!.uid
    private val playerOnTurnIndexFieldName = "playerOnTurnIndex"
    private val questionsFieldName = "questions"
    private val statusNameField = "status"
    private val currentQuesitonIndexFieldName = "currentQuestionIndex"
    private val LOG_TAG = this.javaClass.name
    private var myIndex: Int? = null
    private var myOpponentIndex: Int? = null
    private lateinit var questions : MutableList<MultiplayerGameQuestion>
    var players = mutableListOf<Player>()
    var playerOnTurnIndex = 0
    var status = "bid_open"
    var currentBid = 10
    var listeners = mutableListOf<() -> Unit>()
    var currentQuestionIndex = 0
    //var state : MultiplayerGameState = BidIsOpen

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
                    callSubscribers()

                } else {
                    Log.d(LOG_TAG, "Current data: null")
                }
            }
    }

    val bidIsOpen: Boolean
        get() {
            return status == "bid_open"
        }

    val currentQuestion: MultiplayerGameQuestion
        get() {
            return questions[currentQuestionIndex]
        }

    val playerOnTurn: Player
        get() {
            return players[playerOnTurnIndex]
        }

    val me: Player
        get() {
            if (myIndex != null && myOpponentIndex != null)
            return players[myIndex as Int]
            else {
                throw  PlayerNotInitializedYetException()
            }
        }

    val opponent: Player
        get() {
            if (myIndex != null && myOpponentIndex != null)
                return players[myOpponentIndex as Int]
            else {
                throw  PlayerNotInitializedYetException()
            }
        }

    private fun callSubscribers() {
        for (l in listeners) {
            l()
        }
    }

    private fun updateFromSnapshot(snapshot : DocumentSnapshot) {
        val gameFromDatabase = snapshot
            .toObject(com.francescofricano.midichallenge.models.database.MultiplayerGame::class.java)
        Log.i(LOG_TAG, "Adding questions")
        questions = gameFromDatabase!!.questions.map{
            MultiplayerGameQuestion(
                it.suggestion,
                it.answer,
                it.song
            )
        }.toMutableList()
        Log.i(LOG_TAG, "Adding players")
        players.addAll(gameFromDatabase.players.map { Player(it.id, it.playerName) })
        if (myIndex == null && myOpponentIndex == null) {
            for ((index, p) in players.withIndex()) {
                if (p.id == myUid) {
                    myIndex = index
                }
                else {
                    myOpponentIndex = index
                }
            }
        }
        playerOnTurnIndex = gameFromDatabase.playerOnTurnIndex
        status = gameFromDatabase.status
    }




    private fun switchToNextPlayer() {
        playerOnTurnIndex = (playerOnTurnIndex +1) % players.size
    }

    fun playerOnTurnPasses() {
        currentQuestion.open = false
        switchToNextPlayer()
        status = "bid_closed"
        documentReference.update(
            mapOf(
                playerOnTurnIndexFieldName to playerOnTurnIndex,
                questionsFieldName to questions,
                statusNameField to "bid_closed"
            )
        )
        callSubscribers()
    }

    fun playerOnTurnBids(notes: Int) {
        if (currentBid == 1) {
            throw GameIsClosedException()
        }
        if (notes < currentBid) {
            currentBid= notes
            if (notes == 1) {
                currentQuestion.open = false
                documentReference.update(
                    mapOf(
                        "currentNotes" to notes,
                        playerOnTurnIndexFieldName to playerOnTurnIndex
                    )
                )
            }
            else {
                switchToNextPlayer()
                documentReference.update(
                    mapOf(
                        "currentNotes" to notes,
                        playerOnTurnIndexFieldName to playerOnTurnIndex
                    )
                )
            }
            callSubscribers()
        }
    }

    fun isPlayersTurn(playerID: String) : Boolean {
        return playerID == playerOnTurn.id
    }

    fun addListener(listener: ()-> Unit) {
        listeners.add(listener)
    }

    fun answerCurrentQuestion(answer: String): Boolean {
        val result = currentQuestion.answerQuestion(answer, 1)
        if (result) {
            playerOnTurn.points ++
        }
        else {
            for (p in players) {
                if (p != playerOnTurn) {
                    p.points ++
                }
            }
        }
        currentQuestionIndex ++
        status = "bid_open"
        documentReference.update(mapOf(
            currentQuesitonIndexFieldName to currentQuestionIndex,
            questionsFieldName to questions,
            statusNameField to status
        ))
        return result
    }

    override fun toString(): String {
        return mapOf<String, Any>(
            "currentQuestionIsOpen" to currentQuestion.open,
            "playerOnTurn" to playerOnTurn,
            "currentQuestion" to currentQuestion.suggestion,
            "status" to status
        )
            .toString()
    }

}

sealed class MultiplayerGameState

object BidIsOpen : MultiplayerGameState()
object BidIsClosed: MultiplayerGameState()

class GameIsClosedException (message: String = "The round is already closed") : Exception(message)

class PlayerNotInitializedYetException (message: String = "Player info has not been initialized yet") : Exception(message)
