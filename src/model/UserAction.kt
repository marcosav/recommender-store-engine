package com.gmail.marcosav2010.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class UserAction(
    val user: Long,
    val product: Long,
    val action: Int,
    @Contextual val date: LocalDateTime,
    val delta: Long,
    val value: Int? = null
)

enum class ActionType(val id: Int) {
    CLICK(0),
    FAVORITE(1),
    CART(2),
    BUY(3),
    RATING(4)
}

// evaluator - userInterest
/*data class UserInterest(
    val user: Long,
    val product: Long,
    val score: Double,
    val value: Int,
    val date: Instant,
    val recommender: Double
)

// user-ratings
{
    user: 1,
    product: 1,
    score: 3.2, // when calculated with user actions
    updated: 1234671488,
    recommender: 3.2, // when calculated through recommender
}*/