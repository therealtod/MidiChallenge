package com.example.midichallenge.models

data class Song(val id : Int, val title : String, val author : String, val filename : String)

data class Question(val Song : Song, val suggestion: String, val answer: String)

data class QuestionsList (val questions: List<Question>)

