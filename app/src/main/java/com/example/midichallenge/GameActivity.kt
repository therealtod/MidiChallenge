package com.example.midichallenge

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.midichallenge.models.ClassicGame
import com.example.midichallenge.views.HorizontalNumberPicker

class GameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame
    private val numberPickerMaxValue = 25
    private val numberPickerMinValue = 1
    private var currentQuestionNumber : Int = 0
    private var expectedAnswer = ""


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
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    //This dialog is show to the user after he ans correct
    fun displayDialog( isResponseCorrect : Boolean) {
        val dialogCorrect = Dialog(this@GameActivity)
        dialogCorrect.requestWindowFeature(Window.FEATURE_NO_TITLE)
        if (dialogCorrect.window != null) {
            val colorDrawable = ColorDrawable(Color.TRANSPARENT)
            dialogCorrect.window!!.setBackgroundDrawable(colorDrawable)
        }
        dialogCorrect.setContentView(R.layout.dialog_correct)
        dialogCorrect.setCancelable(false)
        dialogCorrect.show()

        val correctText : View =
            dialogCorrect.findViewById(R.id.correctText)
        val buttonNext: Button =
            dialogCorrect.findViewById(R.id.dialogNext)

        buttonNext.setOnClickListener { view ->
            dialogCorrect.dismiss()
            if(currentQuestionNumber < R.integer.number_of_questions_classic_game_mode) {
                currentQuestionNumber++
                updateView()
            }
            else {
                //crea nuova activity EndGame
                val toast = Toast.makeText(applicationContext, "PARTITA CONCLUSA", 3)
                toast.show()
            }
        }
    }

    fun updateQuestionAndAnswer() {
        val currentQuestion = game.questions.get(currentQuestionNumber)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        expectedAnswer = currentQuestion.answer
        suggestionBox.setText(currentQuestion.suggestion)
    }

    fun updateView() {
        val answerBox: EditText = findViewById(R.id.editText)
        updateQuestionAndAnswer()
        answerBox.setText("")
    }

}
