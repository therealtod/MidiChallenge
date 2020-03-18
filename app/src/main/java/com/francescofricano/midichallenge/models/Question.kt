package com.francescofricano.midichallenge.models


data class ClassicGameQuestion(val sID: Int, val suggestion: String, val answer: String, var points: Int = 0)