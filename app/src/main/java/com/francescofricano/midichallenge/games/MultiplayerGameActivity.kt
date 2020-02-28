package com.francescofricano.midichallenge.games

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.francescofricano.midichallenge.EndGameActivity
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.models.MultiplayerGame
import com.francescofricano.midichallenge.view_components.HorizontalNumberPicker
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
    private lateinit var dialog: Dialog


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
        dialog = Dialog(this@MultiplayerGameActivity)
        val bidButton = findViewById<Button>(R.id.buttonBidMultiplayer)
        val passButton = findViewById<Button>(R.id.buttonPassMultiplayer)
        passButton.setOnClickListener {
            userPerformsAction("pass")
        }
        bidButton.setOnClickListener {
            userPerformsAction("bid")
        }
        initializeView()

    }

    private fun disableControlsForPlayerOnWait() {
        numberPicker.freezeButtons()
        buttonBid.isEnabled = false
        buttonPass.isEnabled = false
    }

    private fun disableControlsBeforeBiddingIsOver() {
        buttonPlayPause.isEnabled = false
        answerBox.isEnabled = false
        confirmButton.isEnabled = false
    }


    private fun initializeView() {
        timer.start()
        disableControlsBeforeBiddingIsOver()
        suggestionTextView.text = game.currentQuestion.suggestion
        if (! isThisPlayerOnTurn()) {
            disableControlsForPlayerOnWait()
            playerOnTurnTextView.text= "IN ATTESA..."
            playerOnTurnTextView.setTextColor(Color.RED)
        }
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
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialog.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialog.setContentView(R.layout.response_dialog)
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun isThisPlayerOnTurn() :Boolean{
        return game.playerOnTurn.id == auth.currentUser!!.uid
    }

    private fun updateView() {
        if (game.currentQuestion.open) {
            disableControlsBeforeBiddingIsOver()
        }
        else {
            buttonBid.isEnabled = false
            buttonPass.isEnabled = false
            buttonPlayPause.isEnabled = true
            answerBox.isEnabled = true
            confirmButton.isEnabled = true
        }
        suggestionTextView.text = game.currentQuestion.suggestion
        if (! isThisPlayerOnTurn()) {
            disableControlsForPlayerOnWait()
            playerOnTurnTextView.text= "IN ATTESA..."
            playerOnTurnTextView.setTextColor(Color.RED)
        }
        else {
            playerOnTurnTextView.text= "É IL TUO TURNO!"
            playerOnTurnTextView.setTextColor(Color.GREEN)
        }
        Thread.sleep(2000)
        dialog.dismiss()
        timer.start()
    }
}