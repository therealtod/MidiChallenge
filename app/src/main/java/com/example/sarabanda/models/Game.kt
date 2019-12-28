package com.example.sarabanda.models

import com.example.sarabanda.R

abstract class Game{
    abstract var questions: ArrayList<Question>
}

class ClassicGame {
    lateinit var questions: ArrayList<ClassicGameQuestion>
    init {
        questions = arrayListOf(ClassicGameQuestion(R.raw.donne, "Successo di Zucchero", "donne"))
    }

}