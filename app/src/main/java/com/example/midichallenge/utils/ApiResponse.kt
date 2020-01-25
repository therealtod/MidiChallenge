package com.example.midichallenge.utils

import com.example.midichallenge.models.Question
import com.example.midichallenge.models.QuestionsList
import com.google.gson.Gson
import org.json.JSONObject
import org.json.JSONTokener


open class ApiResponse(response: String) {

    var success: Boolean = false
    var message: String = ""
    var json: String = ""

    private val data = "data"
    private val msg = "error"

    init {
        try {
            val jsonToken = JSONTokener(response).nextValue()
            if (jsonToken is JSONObject) {
                json = JSONObject(response).toString()

                    success = true
                } else {
                    success = false
                }

            }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

}


class QuestionApiResponse(r: String): ApiResponse(r) {
    private val gson = Gson()
    private var questions = arrayListOf<Question>()
    init {
        try {
            val data = gson.fromJson(r, QuestionsList::class.java)
            this.success = true
            questions.addAll(data.questions)
            println("Parsed successfully as questionslist")
        }
        catch (e: java.lang.Exception) {
            println("Can't interpret as questionslist")
            this.success = false
            questions = arrayListOf()
        }


    }
}