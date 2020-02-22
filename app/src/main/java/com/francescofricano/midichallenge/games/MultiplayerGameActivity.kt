package com.francescofricano.midichallenge.games

import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class MultiplayerGameActivity: SoloGameActivity() {
    private val auth = FirebaseAuth.getInstance()
    lateinit var multiplayerGame: MultiplayerGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 1
        numberPicker.value = numberPicker.max
        playPauseButton.text =  "punta"
        if (game is MultiplayerGame) {
            multiplayerGame = game as MultiplayerGame
            updateView()
            multiplayerGame.addOnChangeListener { updateView() }
        }

    }

    override fun getGame(gameId: String) {
        game = GameRepository.setContext(this).getMultiplayerGame(gameId)
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
        val timerTextView = findViewById<TextView>(R.id.timerTextView)
        if (multiplayerGame.playerOnTurn.id == auth.currentUser!!.uid) {
            object : CountDownTimer(10000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    timerTextView.text = "seconds remaining: " + SimpleDateFormat("mm:ss:SS").format(
                        Date(
                            millisUntilFinished
                        )
                    )
                }

                override fun onFinish() {
                    timerTextView.text = "Tempo scaduto"
                    multiplayerGame.playerOnTurnPasses()
                }
            }.start()
        }
        else {
            disableControls()
        }
    }

}