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
import kotlinx.android.synthetic.main.horizontal_number_picker.view.*


class ClassicGameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame
    private val numberPickerMaxValue = 25
    private val numberPickerMinValue = 0
    private var currentQuestionNumber : Int = 0
    private var expectedAnswer = ""
    private var currentScore = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        currentQuestionNumber = getIntent().getIntExtra("CURRENT_QUESTION", 0)
        game = ClassicGame(this)
        updateView()

        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        numberPicker.max = numberPickerMaxValue
        numberPicker.min = numberPickerMinValue
        val playButton: Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val confirmButton: Button = findViewById(R.id.button2)

        playButton.setOnClickListener{ view ->
            mediaPlayer = MediaPlayer.create(this, R.raw.certe_notti)
            mediaPlayer.start()
            numberPicker.freezebutton()
            }

        confirmButton.setOnClickListener{ view ->
            displayDialog(answerBox.getText().toString() == expectedAnswer)
        }
    }

    override fun onBackPressed() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }


    fun displayDialog( isResponseCorrect : Boolean) {
        val dialog = Dialog(this@ClassicGameActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialog.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialog.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialog.setContentView(R.layout.dialog)
        dialog.setCancelable(false)
        dialog.show()

        val buttonNext: Button =
            dialog.findViewById(R.id.dialogNext)
        val responseText : TextView =
            dialog.findViewById(R.id.responseText)

        if(isResponseCorrect) {
            responseText.setText(R.string.dialog_correct_answer_text)
            responseText.setTextColor(Color.GREEN)
            updateScore()
        }
        else {
            responseText.setText(R.string.dialog_wrong_answer_text)
            responseText.setTextColor(Color.RED)
        }

        buttonNext.setOnClickListener { view ->
            dialog.dismiss()
            if(currentQuestionNumber < this.resources.getInteger(R.integer.number_of_questions_classic_game_mode) - 1) {
                updateView()
            }
            else {
                val intent = Intent(this, EndGameActivity::class.java)
                intent.putExtra("FINAL_SCORE", currentScore)
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
        val answerBox: EditText = findViewById(R.id.editText)
        answerBox.setText("")
        updateQuestionAndAnswer()
    }

    fun updateScore() {
        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        val numberPickerScore = numberPicker.value
        currentScore += numberPickerMaxValue - numberPickerScore
    }

}
