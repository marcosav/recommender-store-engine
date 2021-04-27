package com.gmail.marcosav2010.services.recommender

import com.gmail.marcosav2010.Constants
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.instance

class RecommenderTask(di: DI) {

    private val recommendationLoader by di.instance<RecommendationLoader>()

    suspend fun start() {
        while (true) {
            delay(Constants.RECOMMENDER_UPDATE_DELAY)
            recommendationLoader.execute()
        }
    }
}