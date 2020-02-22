package com.francescofricano.midichallenge.utils

import android.content.Context
import com.francescofricano.midichallenge.models.Question
import com.francescofricano.midichallenge.models.QuestionsList
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

object QuestionManager {
    private val gson: Gson = Gson()
    private lateinit var context: Context


    fun setContext(context: Context): QuestionManager {
        this.context = context
        return this
    }

    fun getNQuestions (number: Int): MutableList<Question> {
        val reader : BufferedReader = BufferedReader(InputStreamReader(context.assets.open("questionsList.json")))
        val questionsList = gson.fromJson(reader, QuestionsList::class.java)
        val questions = questionsList.questions
        val l = ArrayList(questions)
        l.shuffle()
        return l.subList(0, number)
    }
}