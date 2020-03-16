package com.francescofricano.midichallenge.models.database

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MultiplayerGame(
    var playerOnTurnIndex: Int = 0,
    var questions: List<Question> = listOf(),
    var players: List<Player> = listOf(),
    var status: String = "")
