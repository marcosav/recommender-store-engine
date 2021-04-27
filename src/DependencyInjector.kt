package com.gmail.marcosav2010

import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.services.ActionPopulationService
import com.gmail.marcosav2010.services.UserActionService
import com.gmail.marcosav2010.services.recommender.RecommendationLoader
import com.gmail.marcosav2010.services.recommender.RecommendationService
import com.gmail.marcosav2010.services.recommender.calculator.RecommendationCalculator
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.provider
import org.kodein.di.singleton

fun DI.MainBuilder.setupRepositories() {
    bind<UserActionRepository>() with singleton { UserActionRepository(di) }
}

fun DI.MainBuilder.setupServices() {
    bind<ActionPopulationService>() with singleton { ActionPopulationService(di) }

    bind<UserActionService>() with singleton { UserActionService(di) }

    bind<RecommendationLoader>() with singleton { RecommendationLoader(di) }
    bind<RecommendationService>() with singleton { RecommendationService(di) }
    bind<RecommendationCalculator>() with provider { RecommendationCalculator(di) }
}