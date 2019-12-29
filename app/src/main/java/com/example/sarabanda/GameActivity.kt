package com.example.sarabanda

import com.example.sarabanda.views.HorizontalNumberPicker
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.sarabanda.models.ClassicGame

class GameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame
    private val numberPickerMaxValue = 25
    private val numberPickerMinValue = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game = ClassicGame(this)
        val currentQuestion = game.questions.get(0)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        suggestionBox.setText(currentQuestion.suggestion)
        val numberPicker: HorizontalNumberPicker = findViewById(R.id.horizontalNumberPicker)
        numberPicker.max = numberPickerMaxValue
        numberPicker.min = numberPickerMinValue
        val playButton: Button = findViewById(R.id.button7)
        val answerBox: EditText = findViewById(R.id.editText)
        val confirmButton: Button = findViewById(R.id.button2)

    playButton.setOnClickListener{ view ->
            mediaPlayer = MediaPlayer.create(this, currentQuestion.sID)
            mediaPlayer.start()
        }

        confirmButton.setOnClickListener{ view ->
            if(answerBox.getText().toString() == currentQuestion.answer ) {
                val toast = Toast.makeText(applicationContext, "CORRECT ANSWER", Toast.LENGTH_LONG)
                toast.show()
            }
            else {
                val toast = Toast.makeText(applicationContext, "WRONG ANSWER", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }
}
