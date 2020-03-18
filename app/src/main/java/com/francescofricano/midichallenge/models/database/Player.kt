package com.francescofricano.midichallenge.models.database

import com.google.firebase.firestore.IgnoreExtraProperties


@IgnoreExtraProperties
data class Player (val id: String, val playerName: String, val points: Int)