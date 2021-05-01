package com.gmail.marcosav2010.services.recommender

import org.kodein.di.DI
import org.kodein.di.instance
import kotlin.system.exitProcess

class RecommenderTask(di: DI) {

    private val recommendationLoader by di.instance<RecommendationLoader>()

    suspend fun start() {
        while (true) {
            //delay(Constants.RECOMMENDER_UPDATE_DELAY)
            try {
                recommendationLoader.execute()
            } catch (e: Exception) {
                e.printStackTrace(System.err)
                break
            }
            exitProcess(0)
        }
    }
}