package com.gmail.marcosav2010.services.recommender

import com.gmail.marcosav2010.services.recommender.calculator.RecommendationCalculator
import org.kodein.di.DI
import org.kodein.di.provider

class RecommendationLoader(di: DI) {

    val recommendationCalcProvider by di.provider<RecommendationCalculator>()

    fun execute() {
        // calculate user interest
        // save interest for each product-user (last month actions)

        val recommendationCalculator = recommendationCalcProvider()
        recommendationCalculator.execute()

        // store product scores for each user
        // store most visited products (only visited ones, limited to 100)
        // store most similar items (and similarity) for each item (limited to 25)
    }
}