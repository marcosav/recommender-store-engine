package com.gmail.marcosav2010

import com.gmail.marcosav2010.repositories.*
import com.gmail.marcosav2010.services.ActionPopulationService
import com.gmail.marcosav2010.services.UserActionService
import com.gmail.marcosav2010.services.recommender.RecommendationLoader
import com.gmail.marcosav2010.services.recommender.RecommendationService
import com.gmail.marcosav2010.services.recommender.calculator.RecommendationCalculator
import com.gmail.marcosav2010.services.recommender.evaluator.ScoreEvaluator
import com.gmail.marcosav2010.services.recommender.popular.PopularityRanker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

fun DI.MainBuilder.setupRepositories() {
    bind<UserInterestRepository>() with singleton { UserInterestRepository(di) }
    bind<UserActionRepository>() with singleton { UserActionRepository(di) }
    bind<SimilarItemRepository>() with singleton { SimilarItemRepository(di) }
    bind<UserTopItemsRepository>() with singleton { UserTopItemsRepository(di) }
    bind<PopularItemsRepository>() with singleton { PopularItemsRepository(di) }
}

fun DI.MainBuilder.setupServices() {
    bind<ActionPopulationService>() with singleton { ActionPopulationService(di) }

    bind<UserActionService>() with singleton { UserActionService(di) }

    bind<ScoreEvaluator>() with provider { ScoreEvaluator(di) }
    bind<PopularityRanker>() with singleton { PopularityRanker(di) }

    bind<RecommendationLoader>() with singleton { RecommendationLoader(di) }
    bind<RecommendationService>() with singleton { RecommendationService(di) }
    bind<RecommendationCalculator>() with provider { RecommendationCalculator(di) }
}