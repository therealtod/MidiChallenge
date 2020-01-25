package com.example.midichallenge.utils

import android.content.Context
import com.example.midichallenge.models.Question
import com.example.midichallenge.models.QuestionsList
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

object QuestionManager {
    private val questionsList = arrayListOf<Question>()


    fun getQuestions (number: Int): MutableList<Question> {
        val questions = questionsList
        val l = ArrayList(questions)
        l.shuffle()
        return l.subList(0, number)
    }

    fun addQuestions (questions: List<Question>) {
        questionsList.addAll(questions)
    }
}