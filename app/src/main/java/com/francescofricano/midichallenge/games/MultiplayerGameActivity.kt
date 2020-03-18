package com.francescofricano.midichallenge.games

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.models.BidIsClosed
import com.francescofricano.midichallenge.games.models.BidIsOpen
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.view_components.HorizontalNumberPicker
import com.francescofricano.midichallenge.view_components.PlayerScore
import com.francescofricano.midichallenge.view_components.TimeCounter
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*


class MultiplayerGameActivity: AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val LOG_TAG = this.javaClass.name
    private lateinit var timeCounter: TimeCounter
    private lateinit var game :MultiplayerGame
    private lateinit var numberPicker: HorizontalNumberPicker
    private lateinit var buttonBid: Button
    private lateinit var buttonPass: Button
    private lateinit var buttonPlayPause: Button
    private lateinit var confirmButton: Button
    private lateinit var answerBox: EditText
    private lateinit var playerOnTurnTextView: TextView
    private lateinit var suggestionTextView: TextView
    private lateinit var myPointsIndicator: PlayerScore
    private lateinit var opponentPointsIndicator: PlayerScore
    private lateinit var dialog: Dialog
    private lateinit var mediaPlayer: MediaPlayer


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
            if (isThisPlayerOnTurn()) {
                game.playerOnTurnPasses()
                displayTransitionDialog()
            }

        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_multiplayer_game)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        val gameId = intent.getStringExtra("GAME_ID")
        game = GameRepository.setContext(this).getMultiplayerGame(gameId)
        game.addListener {  updateView() }
        numberPicker = findViewById(R.id.horizontalNumberPickerMultiplayer)
        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 1
        numberPicker.value = numberPicker.max
        timeCounter = findViewById(R.id.timeCounterMultiplayer)
        playerOnTurnTextView = findViewById(R.id.textViewPlayerOnTurnMultiplayer)
        confirmButton = findViewById(R.id.buttonConfirmMultiplayer)
        buttonBid = findViewById(R.id.buttonBidMultiplayer)
        buttonPlayPause = findViewById(R.id.buttonPlayMultiplayer)
        answerBox = findViewById(R.id.editTextAnswerMultiplayer)
        buttonPass = findViewById(R.id.buttonPassMultiplayer)
        suggestionTextView = findViewById(R.id.textViewSuggestionMultiplayer)
        myPointsIndicator = findViewById(R.id.PlayerScoreOneMultiplayer)
        opponentPointsIndicator = findViewById(R.id.PlayerScoreTwoMultiplayer)
        mediaPlayer = MediaPlayer.create(this, R.raw.donne_test)
        mediaPlayer.setOnCompletionListener{
            buttonPlayPause.text = "Play"
        }
        dialog =  Dialog(this@MultiplayerGameActivity)
        if (dialog.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialog.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialog.setContentView(R.layout.response_dialog)
        dialog.setCancelable(false)
        buttonPass.setOnClickListener {
            timer.cancel()
            userPerformsAction("pass")
        }
        buttonBid.setOnClickListener {
            timer.cancel()
            userPerformsAction("bid")
        }
        buttonPlayPause.setOnClickListener { view ->
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                buttonPlayPause.text = "Play"
            } else {
                mediaPlayer.start()
                buttonPlayPause.text = "Pause"
            }
        }
        initializeView()
        confirmButton.setOnClickListener{
            game.answerCurrentQuestion(answerBox.text.toString())
        }
    }

    private fun initializeView() {
        if (isThisPlayerOnTurn()) {
            timer.start()
        }
        suggestionTextView.text = game.currentQuestion.suggestion
        updateView()
    }

    private fun userPerformsAction(action: String) {
        timer.cancel()
        when(action) {
            "pass" -> {
                game.playerOnTurnPasses()
            }
            "bid" -> {
                game.playerOnTurnBids(numberPicker.value)
            }
        }
    }

    private fun displayTransitionDialog() {
        val responseText : TextView = dialog.findViewById(R.id.dialogText)
        responseText.text = "In attesa dell'avversario"
        responseText.setTextColor(Color.RED)


        dialog.show()
        /*
        Timer().schedule(object : TimerTask() {
            override fun run() {
                dialog.dismiss()
            }
        }, 2000)

         */
    }


    private fun displayWaitForRivalResponseDialog() {
        val responseText : TextView = dialog.findViewById(R.id.dialogText)
        responseText.text = "L' avversario sta rispondendo"
        responseText.setTextColor(Color.RED)
        dialog.show()
        /*
        Timer().schedule(object : TimerTask() {
            override fun run() {
                dialog.dismiss()
            }
        }, 10000)

         */
    }

    private fun isThisPlayerOnTurn() :Boolean{
        return game.playerOnTurn.id == auth.currentUser!!.uid
    }

    private fun disableEverything() {
        buttonBid.isEnabled = false
        buttonPass.isEnabled = false
        buttonPlayPause.isEnabled = false
        answerBox.isEnabled = false
        confirmButton.isEnabled = false
    }

    private fun updateView() {
        Log.i(LOG_TAG, game.toString())
        disableEverything()
        dialog.dismiss()
        numberPicker.value = game.currentBid
        myPointsIndicator.score = game.me.points
        opponentPointsIndicator.score = game.opponent.points
        if (isThisPlayerOnTurn()) {
            playerOnTurnTextView.text= "É IL TUO TURNO!"
            playerOnTurnTextView.setTextColor(Color.GREEN)
            when (game.bidIsOpen) {
                true -> {
                    buttonBid.isEnabled = true
                    buttonPass.isEnabled = true
                }
                false -> {
                    numberPicker.freezeButtons()
                    buttonPlayPause.isEnabled = true
                    answerBox.isEnabled = true
                    confirmButton.isEnabled = true
                }
            }
        }
        else {
            playerOnTurnTextView.text= "IN ATTESA..."
            playerOnTurnTextView.setTextColor(Color.RED)
            when (game.bidIsOpen) {
                true -> {
                    displayTransitionDialog()
                }
                false -> {
                    displayWaitForRivalResponseDialog()
                }
            }
        }
    }
}
