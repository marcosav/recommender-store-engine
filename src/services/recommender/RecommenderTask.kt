package com.gmail.marcosav2010.services.recommender

import com.gmail.marcosav2010.Constants
import com.gmail.marcosav2010.services.ActionPopulationService
import kotlinx.coroutines.delay
import org.kodein.di.DI
import org.kodein.di.instance

class RecommenderTask(di: DI) {

    private val recommendationLoader by di.instance<RecommendationLoader>()

    private val actionPopulationService by di.instance<ActionPopulationService>()

    suspend fun start() {
        if (System.getenv(Constants.POPULATE) == "yes")
            actionPopulationService.generate()

        while (true) {
            try {
                recommendationLoader.execute()
            } catch (e: Exception) {
                e.printStackTrace(System.err)
                break
            }

            delay(Constants.RECOMMENDER_UPDATE_DELAY)
        }
    }
}