package com.gmail.marcosav2010.services.recommender

import com.gmail.marcosav2010.services.recommender.calculator.RecommendationCalculator
import com.gmail.marcosav2010.services.recommender.evaluator.ScoreEvaluator
import com.gmail.marcosav2010.services.recommender.popular.PopularityRanker
import org.kodein.di.DI
import org.kodein.di.instance
import org.kodein.di.provider

class RecommendationLoader(di: DI) {

    private val recommendationCalcProvider by di.provider<RecommendationCalculator>()
    private val scoreEvaluatorProvider by di.provider<ScoreEvaluator>()

    private val popularityRanker by di.instance<PopularityRanker>()

    fun execute() {
        val scoreEvaluator = scoreEvaluatorProvider()
        scoreEvaluator.execute()

        val recommendationCalculator = recommendationCalcProvider()
        recommendationCalculator.execute()

        popularityRanker.execute()
    }
}