package com.francescofricano.midichallenge.games

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.view_components.TimeCounter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class MultiplayerGameActivity: SoloGameActivity() {
    private val auth = FirebaseAuth.getInstance()
    lateinit var multiplayerGame: MultiplayerGame
    private val LOG_TAG = this.javaClass.name
    private lateinit var timeCounter: TimeCounter
    private val timer = object : CountDownTimer(10000, 1) {
        override fun onTick(millisUntilFinished: Long) {
            val timeAsString = SimpleDateFormat("ss.SS").format(
                Date(
                    millisUntilFinished
                ))
            timeCounter.setTime(timeAsString)
        }

        override fun onFinish() {
            timeCounter.setTime("00.00")
            multiplayerGame.playerOnTurnPasses()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 1
        numberPicker.value = numberPicker.max
        timeCounter = findViewById(R.id.timeCounter)
        if (game is MultiplayerGame) {
            multiplayerGame.addOnChangeListener { updateView() }
        }
        val bidButton = findViewById<Button>(R.id.buttonBid)
        val passButton = findViewById<Button>(R.id.buttonPass)
        passButton.setOnClickListener {
            userPerformsAction("pass")
        }
        bidButton.setOnClickListener {
            userPerformsAction("bid")
        }

    }

    override fun getGame(gameId: String) {
        Log.i(LOG_TAG, "Trying to retrieve: ${gameId}")
        game = GameRepository.setContext(this).getMultiplayerGame(gameId)
        multiplayerGame = game as MultiplayerGame
    }

    private fun disableControls() {
        numberPicker.freezeButtons()
        confirmButton.isEnabled = false
        playPauseButton.isEnabled = false
        answerBox.isEnabled = false
    }

    override fun setLayout() {
        setContentView(R.layout.activity_multiplayer_game)
    }

    override fun updateView() {
        super.updateView()
        timer.start()
        if (multiplayerGame.playerOnTurn.id == auth.currentUser!!.uid) {
            disableControls()
            val playerOnTurnTextView = findViewById<TextView>(R.id.playerOnTurnTextView)
            playerOnTurnTextView.text= "IN ATTESA..."
            playerOnTurnTextView.setTextColor(Color.RED)
        }
    }

    private fun userPerformsAction(action: String) {
        timer.cancel()
        when(action) {
            "pass" -> {
                multiplayerGame.playerOnTurnPasses()
            }
            "bid" -> {
                multiplayerGame.playerOnTurnBids(numberPicker.value)
            }
        }
    }
}