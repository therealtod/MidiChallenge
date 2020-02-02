package com.francescofricano.midichallenge.models

import com.google.firebase.auth.FirebaseUser

data class MidichallengeUser (val user: FirebaseUser, val friends: List<MidichallengeUser>)