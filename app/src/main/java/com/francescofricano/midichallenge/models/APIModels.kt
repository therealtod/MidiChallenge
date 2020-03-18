package com.francescofricano.midichallenge.models

import com.francescofricano.midichallenge.models.database.Song


data class Question(val song : Song, val suggestion: String, val answer: String)

data class QuestionsList (val questions: List<Question>)

data class MultiplayerQuestion(
    val answer: String,
    val song: Song,
    val suggestion: String
)

data class MultiplayerGameFromApi(
    val playerOnTurnIndex: Int,
    val players: List<String>,
    val questions: List<MultiplayerQuestion>,
    val status: String)

