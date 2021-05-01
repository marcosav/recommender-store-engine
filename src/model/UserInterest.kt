package com.gmail.marcosav2010.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInterest(
    val user: Long,
    val item: Long,
    val score: Double? = null,
    val predicted: Double? = null
) {
    init {
        require(score != null || predicted != null)
    }
}