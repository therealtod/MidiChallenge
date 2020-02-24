package com.francescofricano.midichallenge.ui.home

import android.content.Intent
import android.graphics.Typeface
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
import com.google.firebase.firestore.ListenerRegistration

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
        //val textView: TextView = root.findViewById(R.id.text_home)
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            //textView.text = it
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

    private fun waitForGame()  {
        if (auth.currentUser != null) {
            /* Query all the games where my id is included in "players"
             * and the status is "waiting". Get only one.
             */
            Log.i(LOG_TAG, "Before building the query")
            db.collection(gamesCollectionName)
                .whereArrayContains(playersListFieldName, auth.currentUser!!.uid)
                .whereEqualTo("status", "waiting")
                .limit(1)
                .addSnapshotListener()
                // When the query is executed successfully
                    { snapshot, err ->
                    Log.i(LOG_TAG, "I've found a new game waiting ")
                    // If the query found something
                    if (snapshot != null && !snapshot.isEmpty) {
                        Log.i(LOG_TAG, "We have found the following documents:")
                        val gameDocument = snapshot.documents[0]
                        Log.i(LOG_TAG, "${gameDocument.data}")
                        val gameRef = gameDocument.reference
                        val gameId = GameRepository
                            .setContext(activity!!)
                            .newMultiplayerGame(gameDocument)
                        val intent = Intent(activity, MultiplayerGameActivity::class.java)
                        intent.putExtra("GAME_ID", gameId)
                        startActivity(intent)
                        gameRef.update(mapOf(
                            "status" to "ongoing"
                        ))
                                // If the update was successful
                            .addOnCompleteListener {
                                // Listen to changes to the game document
                                gameRef.addSnapshotListener {snapshot, err ->
                                    if (err != null) {
                                        Log.w(LOG_TAG, "Can't listen to this document")
                                        return@addSnapshotListener
                                    }
                                    if (snapshot != null && snapshot.exists()) {
                                        Log.d(LOG_TAG, "Current data: ${snapshot.data}")
                                        GameRepository.getMultiplayerGame(gameId)
                                            .updateFromDatabaseData(snapshot.data)

                                    } else {
                                        Log.d(LOG_TAG, "An error occurred: Maybe the document doesn't exist anymore?")
                                    }


                                }
                            }

                    }
                    else {
                        Log.d(LOG_TAG, "No games waiting for current user")
                    }
                }
        }
    }
}