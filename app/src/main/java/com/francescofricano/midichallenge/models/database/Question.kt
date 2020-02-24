package com.francescofricano.midichallenge.models.database


data class Question (
    var answer: String="",
    var song: Song?=null,
    var suggestion: String="")