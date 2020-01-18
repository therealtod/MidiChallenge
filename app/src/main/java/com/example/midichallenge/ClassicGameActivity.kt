package com.example.midichallenge

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.midichallenge.models.ClassicGame
import com.example.midichallenge.view_components.HorizontalNumberPicker


class ClassicGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame
    private var currentQuestionNumber : Int = 0
    private var expectedAnswer = ""
    private var isPlayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_game)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        var notesUsed = 0
        val playButton: Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val confirmButton: Button = findViewById(R.id.button2)

        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 0
        currentQuestionNumber = getIntent().getIntExtra("CURRENT_QUESTION", 0)
        game = ClassicGame(this)

        updateQuestionAndAnswer()
        updateView()

        mediaPlayer = MediaPlayer.create(this, R.raw.donne_test)

        playButton.setOnClickListener{ view ->
            isPlayed = true
            notesUsed = numberPicker.value
            mediaPlayer.start()
            numberPicker.freezeButtons()
            }

        confirmButton.setOnClickListener{ view ->
            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            responseDialog(answerBox.getText().toString() == expectedAnswer, notesUsed, isPlayed)
        }

    }

    /*
    override fun onBackPressed() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }
    */

    override fun onBackPressed(): Unit {
        AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setIconAttribute(android.R.attr.alertDialogIcon)
            .setTitle("Chiusura dell'attività")
            .setMessage("I tuoi progressi non saranno salvati,\nsei sicuro di voler uscire?")
            .setPositiveButton(
                "Yes"
            ) { dialog, which ->
                if(mediaPlayer.isPlaying) {
                    mediaPlayer.stop()
                }
                finish() }
            .setNegativeButton("No", null)
            .show()
    }

    fun updateQuestionAndAnswer() {
        currentQuestionNumber++
        val currentQuestion = game.questions.get(currentQuestionNumber)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        expectedAnswer = currentQuestion.answer
        suggestionBox.setText(currentQuestion.suggestion)
    }

    fun updateView() {
        val currentPoints: TextView = findViewById(R.id.textViewPlayerScore)
        val counterQuestionBox: TextView = findViewById(R.id.textViewQuestionCounter)
        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        val answerBox: EditText = findViewById(R.id.editText)
        currentPoints.setText(game.getScore().toString())
        counterQuestionBox.setText((currentQuestionNumber + 1).toString() + " / " + resources.getInteger(R.integer.number_of_questions_classic_game_mode).toString())
        numberPicker.value = 0
        numberPicker.unfreezeButtons()
        isPlayed = false
        answerBox.setText("")
    }

    fun responseDialog(isResponseCorrect : Boolean, notesUsed: Int, isPlayed : Boolean) {
        val dialog = Dialog(this@ClassicGameActivity)
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
            game.answerQuestion(notesUsed, currentQuestionNumber, isPlayed)
        }
        else {
            responseText.setText(R.string.dialog_wrong_answer_text)
            responseText.setTextColor(Color.RED)
        }

        buttonNext.setOnClickListener { view ->
            dialog.dismiss()
            if(currentQuestionNumber < this.resources.getInteger(R.integer.number_of_questions_classic_game_mode) - 1) {
                updateQuestionAndAnswer()
                updateView()
            }
            else {
                val intent = Intent(this, EndGameActivity::class.java)
                intent.putExtra("FINAL_SCORE", game.getScore())
                startActivity(intent)
                finish()
            }
        }
    }

}
