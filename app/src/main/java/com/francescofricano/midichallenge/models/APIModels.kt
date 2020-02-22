package com.francescofricano.midichallenge.models

data class Song(val id : Int, val title : String, val author : String, val filename : String?) {
    companion object {
        fun fromHashMap(map : HashMap<String, Any>) : Song{
            val id = map.get("id").toString().toInt()
            val title = map.get("title") as String
            val author = map.get("author") as String
            val filename = map.get("filename") as String?
            return Song(id, title, author, filename)
        }
    }

}

data class Question(val song : Song, val suggestion: String, val answer: String)

data class QuestionsList (val questions: List<Question>)

