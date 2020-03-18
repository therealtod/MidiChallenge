package com.francescofricano.midichallenge.models

import android.content.Context
import com.firebase.ui.auth.data.model.User
import com.francescofricano.midichallenge.R
import com.francescofricano.midichallenge.utils.QuestionManager
import com.google.firebase.auth.FirebaseAuth
import java.lang.Error



data class RandomMatchGame (val player1: MidichallengeUser,
                            val player2: MidichallengeUser?,
                            val status: RandomMatchGameStatus) {
    companion object {
        abstract class RandomMatchGameStatus
        class Open : RandomMatchGameStatus()
        class Closed: RandomMatchGameStatus()
    }
}
