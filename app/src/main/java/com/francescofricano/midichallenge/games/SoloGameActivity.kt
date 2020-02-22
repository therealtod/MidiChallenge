package com.francescofricano.midichallenge.games

import com.francescofricano.midichallenge.EndGameActivity
import com.francescofricano.midichallenge.R


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.francescofricano.midichallenge.games.models.SoloGame
import com.francescofricano.midichallenge.view_components.HorizontalNumberPicker
import kotlinx.android.synthetic.main.horizontal_number_picker.view.*


open class SoloGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    lateinit var game: SoloGame
    private var isPlayed = false
    lateinit var numberPicker: HorizontalNumberPicker
    lateinit var playPauseButton : Button
    lateinit var answerBox: EditText
    lateinit var confirmButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLayout()
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        val title = findViewById<TextView>(R.id.imageView2)
        val tf = Typeface.createFromAsset(assets, "fonts/julius_sans_one_regular.ttf")
        title.typeface = tf
        window.setBackgroundDrawableResource(R.drawable.sfondo_7x15_300dpi)
        numberPicker= findViewById(R.id.horizontalNumberPicker)
        playPauseButton = findViewById(R.id.button7)
        answerBox = findViewById(R.id.editText)
        confirmButton = findViewById(R.id.button2)

        var notesUsed = 0

        playPauseButton.isEnabled = false
        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 0
        numberPicker.et_number.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
                playPauseButton.isEnabled = (numberPicker.value != 0)
            }
            override fun beforeTextChanged(cs: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun afterTextChanged(arg0: Editable) {}
        })
        // We retrieve the gameId assigned to this execution of the activity
        val gameId = intent.getStringExtra("GAME_ID")
            ?: throw Exception ("Game Id not found in intent extra")
        //And retrieve the game from the GameRepository using this gameId
        getGame(gameId)
        updateView()

        mediaPlayer = MediaPlayer.create(this, R.raw.donne_test)
        mediaPlayer.setOnCompletionListener{
            playPauseButton.text = "Play"
        }

        playPauseButton.setOnClickListener{ view ->
            isPlayed = true
            numberPicker.freezeButtons()
            notesUsed = numberPicker.value
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                playPauseButton.text = "Play"
            } else {
                mediaPlayer.start()
                playPauseButton.text = "Pause"
            }
        }

        confirmButton.setOnClickListener{
            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            // Give the answer and get the result
            val result = game.answerCurrentQuestion(answerBox.text.toString(), notesUsed)
            game.goToNextQuestion()
            displayResponseDialog(result)
        }
    }

    override fun onBackPressed(): Unit {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle("Stai uscendo dal gioco")
            .setMessage("I tuoi progressi non saranno salvati, sei sicuro?")
            .setPositiveButton("Yes") { dialog, which ->
                if(mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                finish() }
            .setNegativeButton("No", null)
            .show()
    }



    open fun updateView() {
        val currentPoints: TextView = findViewById(R.id.textViewPlayerScore)
        val counterQuestionBox: TextView = findViewById(R.id.textViewQuestionCounter)
        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        val playPauseButton : Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        suggestionBox.text = game.getCurrentQuestion().suggestion
        currentPoints.text = game.score.toString()
        counterQuestionBox.text =(game.currentQuestionNumber).toString() + " / " +  game.numberOfQuestions
        isPlayed = false
        numberPicker.value = 0
        numberPicker.unfreezeButtons()
        playPauseButton.isEnabled = false
        answerBox.setText("")
    }

    private fun displayResponseDialog(isResponseCorrect : Boolean) {
        val dialog = Dialog(this@SoloGameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialog.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialog.setContentView(R.layout.response_dialog)
        dialog.setCancelable(false)
        dialog.show()

        val buttonNext: Button = dialog.findViewById(R.id.responseDialogNextButton)
        val responseText : TextView = dialog.findViewById(R.id.responseDialogText)

        if(isResponseCorrect) {
            responseText.setText(R.string.dialog_correct_answer_text)
            responseText.setTextColor(Color.GREEN)
        }
        else {
            responseText.setText(R.string.dialog_wrong_answer_text)
            responseText.setTextColor(Color.RED)
        }

        buttonNext.setOnClickListener {
            dialog.dismiss()
            if(!game.isOver()) {
                updateView()
            }
            else {
                val intent = Intent(this, EndGameActivity::class.java)
                intent.putExtra("FINAL_SCORE", game.score)
                startActivity(intent)
                finish()
            }
        }
    }
    open fun getGame(gameId: String) {
        game = GameRepository.setContext(this).getSoloGame(gameId)
    }

    open fun setLayout() {
        setContentView(R.layout.activity_classic_game)
    }
}