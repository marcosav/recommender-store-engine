package com.gmail.marcosav2010.services.recommender

import org.kodein.di.DI

class RecommendationService(di: DI) {

    fun forUser(user: Long): Iterable<Long> {
// existing recommendations for user x
        return listOf(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L)
    }

    fun forItem(item: Long): Iterable<Long> {
// existing recommendations for item x
        return listOf(11, 22, 33, 44, 55, 66, 77, 88, 99L)
    }

    fun popular(): Iterable<Long> {
        return listOf(114, 221, 133, 414, 515, 616, 771, 818, 919L)
    }
}