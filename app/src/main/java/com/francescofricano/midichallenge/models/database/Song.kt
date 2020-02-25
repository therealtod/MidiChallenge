package com.francescofricano.midichallenge.models.database

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Song(var id : Int=0, var title : String="", var author : String="", var filename : String?="") {
    /*
    companion object {
        fun fromHashMap(map : HashMap<String, Any>) : Song{
            val id = map.get("id").toString().toInt()
            val title = map.get("title") as String
            val author = map.get("author") as String
            val filename = map.get("filename") as String?
            return Song(id, title, author, filename)
        }
    }
    */
}