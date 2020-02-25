package com.francescofricano.midichallenge.models.database

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Question (
    var answer: String="",
    var song: Song?=null,
    var suggestion: String="")