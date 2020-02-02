package com.francescofricano.midichallenge

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
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
import com.francescofricano.midichallenge.models.ClassicGame
import com.francescofricano.midichallenge.view_components.HorizontalNumberPicker
import kotlinx.android.synthetic.main.horizontal_number_picker.view.*


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
        val playPauseButton : Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val confirmButton: Button = findViewById(R.id.button2)

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

        currentQuestionNumber = getIntent().getIntExtra("CURRENT_QUESTION", 0)
        game = ClassicGame(this)

        updateQuestionAndAnswer()
        updateView()

        mediaPlayer = MediaPlayer.create(this, R.raw.donne_test)
        mediaPlayer.setOnCompletionListener{
            playPauseButton.setText("Play")
        }

        playPauseButton.setOnClickListener{ view ->
                isPlayed = true
                numberPicker.freezeButtons()
                notesUsed = numberPicker.value
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                    playPauseButton.setText("Play")
                } else {
                    mediaPlayer.start()
                    playPauseButton.setText("Pause")
                }
        }

        confirmButton.setOnClickListener{ view ->
            if(mediaPlayer.isPlaying) {
                mediaPlayer.stop()
            }
            responseDialog(answerBox.getText().toString() == expectedAnswer, notesUsed, isPlayed)
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
        val playPauseButton : Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        currentPoints.setText(game.getScore().toString())
        counterQuestionBox.setText((currentQuestionNumber + 1).toString() + " / " + resources.getInteger(R.integer.number_of_questions_classic_game_mode).toString())
        isPlayed = false
        numberPicker.value = 0
        numberPicker.unfreezeButtons()
        playPauseButton.isEnabled = false
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
