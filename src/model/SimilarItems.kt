package com.gmail.marcosav2010.model

import kotlinx.serialization.Serializable

@Serializable
data class SimilarItem(
    val item: Long,
    val score: Double
)

@Serializable
data class SimilarItems(
    val item: Long,
    val similar: List<SimilarItem>
) : PreliminaryEntry()