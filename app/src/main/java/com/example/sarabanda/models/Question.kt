package com.example.sarabanda.models

open class Question(val songID: Int)

data class ClassicGameQuestion(val sID: Int, val suggestion: String, val answer: String): Question(sID)