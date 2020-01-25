package com.example.midichallenge.utils

import android.content.Context
import com.android.volley.Request
import com.example.midichallenge.models.Song

sealed class ApiRoute {

    val timeOut: Int
        get() {
            return 10000
        }


    private val baseUrl: String
        get() {
            return "https://midichallengeapi.herokuapp.com"
        }

    data class Login(var email: String, var password: String, var ctx: Context) : ApiRoute()
    data class GetUser(var ctx: Context) : ApiRoute()
    data class GetFeature(var householdID: Int, var ctx: Context) : ApiRoute()
    class Question : ApiRoute()

    val httpMethod: Int
        get() {
            return when (this) {
                is Login -> Request.Method.POST
                is GetUser -> Request.Method.GET
                is GetFeature -> Request.Method.GET
                is Question -> Request.Method.GET
            }
        }

    val params: HashMap<String, String>
        get() {
            return when (this) {
                is Login -> {
                    hashMapOf(Pair("email", this.email), Pair("password", this.password))
                }
                else -> hashMapOf()
            }
        }


    val headers: HashMap<String, String>
        get() {
            val map: HashMap<String, String> = hashMapOf()
            map["Accept"] = "application/json"
            return when (this) {
                is GetUser -> {
                    //map["Authorization"] = "Bearer ${UserDefaults(this.ctx).accessToken}"
                    map
                }
                is GetFeature -> {
                    //map["Authorization"] = "Bearer ${UserDefaults(this.ctx).accessToken}"
                    map
                }
                else -> map
            }
        }


    val url: String
        get() {
            return "$baseUrl/${when (this@ApiRoute) {
                is Login -> "account/login"
                is GetUser -> "account/profile"
                is GetFeature -> "household/$householdID/feature"
                is Question -> "api/question/"
            }}"
        }
}