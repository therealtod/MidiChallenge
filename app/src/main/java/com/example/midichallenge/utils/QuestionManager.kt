package com.example.midichallenge.utils

import android.content.Context
import com.example.midichallenge.models.Question
import com.example.midichallenge.models.QuestionsList
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class QuestionManager(val context: Context) {
    private val gson: Gson = Gson()
    private val questionsList: QuestionsList
    init{
        val reader : BufferedReader = BufferedReader(InputStreamReader(context.assets.open("songList.json")))
        questionsList = gson.fromJson(reader, QuestionsList::class.java)
    }

    fun getQuestions (number: Int): MutableList<Question> {
        val questions = questionsList.questions
        val l = ArrayList(questions)
        l.shuffle()
        return l.subList(0, number)
    }
}