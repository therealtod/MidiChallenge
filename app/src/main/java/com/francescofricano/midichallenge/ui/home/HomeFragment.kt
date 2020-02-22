package com.francescofricano.midichallenge.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.games.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val gamesCollectionName = "games"
    private val playersListFieldName = "players"
    private val LOG_TAG = this.javaClass.name

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
            updateView()
            val randomChallengeButton = view!!.findViewById<Button>(R.id.random_match_button)
            randomChallengeButton.setOnClickListener {
                queueForRandomOpponentMatch()
            }
            val soloGameButton = view!!.findViewById<Button>(R.id.solo_game_button)
            soloGameButton.setOnClickListener {
                val gameId = GameRepository.setContext(activity!!.applicationContext).newSoloGame()
                val intent = Intent(activity, SoloGameActivity::class.java)
                intent.putExtra("GAME_ID", gameId)
                startActivity(intent)
            }
        })
        waitForGame()
        return root
    }


    override fun onResume() {
        super.onResume()
        updateView()
    }

    private fun queueForRandomOpponentMatch() {
        MatchManager.randomQueue()
    }

    private fun updateView() {
        val randomChallengeButton = view!!.findViewById<Button>(R.id.random_match_button)
        if (FirebaseAuth.getInstance().currentUser != null) {
            randomChallengeButton.visibility = View.VISIBLE
        }
        else {
            randomChallengeButton.visibility = View.INVISIBLE
        }
    }

    private fun waitForGame() {
        if (auth.currentUser != null) {
            db.collection(gamesCollectionName)
                .whereArrayContains(playersListFieldName, auth.currentUser!!.uid)
                .limit(1)
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.w(LOG_TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val gameReference = snapshot.documents[0].reference
                        gameReference.update(mapOf(
                            "status" to "ongoing"
                        ))
                        gameReference.get().addOnSuccessListener {document ->
                            val gameId = GameRepository
                                .setContext(context!!).newMultiplayerGame(document)
                            val game = GameRepository.getMultiplayerGame(gameId)
                            game.addOnChangeListener {
                            }
                            val intent = Intent(activity, MultiplayerGameActivity::class.java)
                            intent.putExtra("GAME_ID", gameId)
                            startActivity(intent)
                        }


                    } else {
                        Log.d(LOG_TAG, "Current data: null")
                    }
                }
        }
    }
}