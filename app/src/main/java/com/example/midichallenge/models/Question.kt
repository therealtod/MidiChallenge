package com.example.midichallenge.models

open class Question(val file: String, val author: String, val suggestion: String, val answer: String)

data class ClassicGameQuestion(val sID: Int, val suggestion: String, val answer: String)