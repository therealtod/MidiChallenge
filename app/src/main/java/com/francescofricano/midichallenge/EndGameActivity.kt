package com.francescofricano.midichallenge

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.francescofricano.midichallenge.games.SoloGameActivity

class EndGameActivity : AppCompatActivity() {

    private var finalScore :Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)

        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {}

        val scoreTextView : TextView = findViewById(R.id.endGameScore)
        val playAgainButton: Button = findViewById(R.id.playAgainButton)

        finalScore = intent.getIntExtra("FINAL_SCORE", 0)
        scoreTextView.text = finalScore.toString()

        playAgainButton.setOnClickListener { view ->
            val intent = Intent(this, SoloGameActivity::class.java)
            intent.putExtra("CURRENT_QUESTION", -1)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val openMainActivity = Intent(this, MainActivity::class.java)
        openMainActivity.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
        startActivityIfNeeded(openMainActivity, 0)
        finish()
    }
}