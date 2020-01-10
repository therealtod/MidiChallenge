package com.example.midichallenge.models

data class Question(val sID: Int, val suggestion: String, val answer: String)

data class QuestionsList (val questions: List<Question>)

