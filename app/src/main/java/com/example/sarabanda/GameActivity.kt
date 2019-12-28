package com.example.sarabanda

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.sarabanda.models.ClassicGame
import com.example.sarabanda.models.Game


class GameActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var game: ClassicGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        game = ClassicGame()
        val currentQuestion = game.questions.get(0)
        val suggestionBox: TextView = findViewById(R.id.textView2)
        suggestionBox.setText(currentQuestion.suggestion)
        val playButton: Button = findViewById(R.id.button7)
        val confirmButton: Button = findViewById(R.id.button2)
        val answerBox: EditText = findViewById(R.id.editText)

        playButton.setOnClickListener{ view ->
            mediaPlayer = MediaPlayer.create(this, currentQuestion.sID)
            mediaPlayer.start()
        }

        confirmButton.setOnClickListener{ view ->
            if(answerBox.getText().toString() == currentQuestion.answer ) {
                val toast = Toast.makeText(applicationContext, "Risposta corretta", Toast.LENGTH_LONG)
                toast.show()
            }
            else {
                val toast = Toast.makeText(applicationContext, "Risposta errata", Toast.LENGTH_LONG)
                toast.show()
            }
        }
    }
}
