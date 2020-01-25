package com.example.midichallenge

import android.app.Application
import com.example.midichallenge.models.QuestionsList
import com.example.midichallenge.utils.ApiClient
import com.example.midichallenge.utils.QuestionManager
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader

class MidiChallengeApp: Application(){
    private val apiClient: ApiClient
    private val questionManager = QuestionManager
    private val gson = Gson()

    init {
        apiClient = ApiClient(this)

    }

    override fun onCreate() {
        super.onCreate()
        val reader : BufferedReader = BufferedReader(InputStreamReader(this.assets.open("questionsList.json")))
        val questions = gson.fromJson(reader, QuestionsList::class.java)
        if (questions != null) {
            questionManager.addQuestions(questions.questions)
        }
        apiClient.fetchQuestions{
            questionsList, message ->
            if (questionsList != null) {
                questionManager.addQuestions(questionsList.questions)
            }
        }
    }
}