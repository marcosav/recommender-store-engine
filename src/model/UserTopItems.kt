package com.gmail.marcosav2010.model

import kotlinx.serialization.Serializable

@Serializable
data class UserTopItem(
    val item: Long,
    val score: Double
)

@Serializable
data class UserTopItems(
    val user: Long,
    val items: List<UserTopItem>
)