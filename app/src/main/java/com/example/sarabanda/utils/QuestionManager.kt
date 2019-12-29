package com.example.sarabanda.utils

import android.content.Context
import com.example.sarabanda.models.Question
import org.json.JSONObject
import java.io.InputStream

class QuestionManager (val context : Context) {
    private lateinit var songList: ArrayList<Question>
    init{
        val jsonFile : InputStream = context.assets.open("songList.json")
        val size : Int = jsonFile.available()
        val buffer : ByteArray = ByteArray(size)
        jsonFile.read(buffer)
        jsonFile.close()
        val jsonString: String = String(buffer)
        val obj : JSONObject = JSONObject(jsonString)
    }
}