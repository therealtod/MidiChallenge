package com.francescofricano.midichallenge.games

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object MatchManager {
    private val db = FirebaseFirestore.getInstance()
    private val challengesCollectionName = "challenges"
    private val gamesCollectionName = "games"
    private val playerKeyName = "playerID"
    private val LOG_TAG = this.javaClass.name
    private val auth = FirebaseAuth.getInstance()


    fun randomQueue (){
        //Look for oldest open room not created by the current user
        db.collection(
            challengesCollectionName
        )
            .document(auth.currentUser!!.uid)
            .set(Challenge(auth.currentUser!!.uid))
    }
}