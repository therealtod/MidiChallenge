package com.francescofricano.midichallenge.games.models

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class Player (val id: String, val name: String, var points: Int = 0)