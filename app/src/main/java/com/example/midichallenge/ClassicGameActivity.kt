package com.example.midichallenge

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
import com.example.midichallenge.views.HorizontalNumberPicker


class ClassicGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame
    private var currentQuestionNumber : Int = 0
    private var expectedAnswer = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classic_game)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        val notesUsed = numberPicker.value
        val playButton: Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val confirmButton: Button = findViewById(R.id.button2)

        numberPicker.max = resources.getInteger(R.integer.max_number_notes_classic_game_mode)
        numberPicker.min = 0
        currentQuestionNumber = getIntent().getIntExtra("CURRENT_QUESTION", 0)
        game = ClassicGame(this)
        updateQuestionAndAnswer()
        updateView()

        playButton.setOnClickListener{ view ->
            mediaPlayer = MediaPlayer.create(this, R.raw.certe_notti)
            mediaPlayer.start()
            numberPicker.freezeButtons()
            }

        confirmButton.setOnClickListener{ view ->
            mediaPlayer.stop()
            displayDialog(answerBox.getText().toString() == expectedAnswer, notesUsed)
        }

    }

    override fun onBackPressed() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }

    fun displayDialog( isResponseCorrect : Boolean, notesUsed: Int) {
        val dialog = Dialog(this@ClassicGameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialog.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialog.setContentView(R.layout.dialog)
        dialog.setCancelable(false)
        dialog.show()

        val buttonNext: Button = dialog.findViewById(R.id.dialogNextButton)
        val responseText : TextView = dialog.findViewById(R.id.responseText)

        if(isResponseCorrect) {
            responseText.setText(R.string.dialog_correct_answer_text)
            responseText.setTextColor(Color.GREEN)
            game.answerQuestion(notesUsed, currentQuestionNumber)
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

    fun updateQuestionAndAnswer() {
        currentQuestionNumber++
        val currentQuestion = game.questions.get(currentQuestionNumber)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        expectedAnswer = currentQuestion.answer
        suggestionBox.setText(currentQuestion.suggestion)
    }

    fun updateView() {
        val currentPoints: TextView = findViewById(R.id.textView5)
        val counterQuestionBox: TextView = findViewById(R.id.textView4)
        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        val answerBox: EditText = findViewById(R.id.editText)
        currentPoints.setText(game.getScore().toString())
        counterQuestionBox.setText((currentQuestionNumber + 1).toString())
        numberPicker.value = 0
        numberPicker.unfreezeButtons()
        answerBox.setText("")
    }

}
