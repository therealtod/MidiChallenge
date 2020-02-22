package com.francescofricano.midichallenge.utils

import com.google.common.reflect.TypeToken
import com.google.gson.Gson

interface FirebaseWritable {
    fun toHashMap()
}

abstract class AbstractFirebaseWritable : FirebaseWritable {
     val gson = Gson()

    //convert a data class to a map
    fun <T> T.serializeToMap(): Map<String, Any> {
        return convert()
    }

    //convert a map to a data class
    inline fun <reified T> Map<String, Any>.toDataClass(): T {
        return convert()
    }

    //convert an object of type I to type O
    inline fun <I, reified O> I.convert(): O {
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<O>() {}.type)
    }
}