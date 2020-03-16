package com.francescofricano.midichallenge.games

import com.francescofricano.midichallenge.utils.TimestampUtils


data class Challenge(
    val playerID: String,
    val playerName: String,
    val createdAt: String = TimestampUtils.iSO8601StringForCurrentDate)