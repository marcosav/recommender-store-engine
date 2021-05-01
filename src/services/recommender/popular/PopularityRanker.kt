package com.gmail.marcosav2010.services.recommender.popular

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.PopularItem
import com.gmail.marcosav2010.model.RankType
import com.gmail.marcosav2010.repositories.PopularItemsRepository
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDateTime

class PopularityRanker(di: DI) {

    companion object {
        val ACTION_TYPES = listOf(ActionType.VISIT)
    }

    private val popularItemsRepository by di.instance<PopularItemsRepository>()
    private val userActionRepository by di.instance<UserActionRepository>()

    fun execute() {
        popularItemsRepository.clearAll()

        val now = LocalDateTime.now()

        RankType.values().forEach { r ->
            ACTION_TYPES.forEach { a ->
                userActionRepository.findMost(a, now.minus(r.period), 100).forEach { i ->
                    popularItemsRepository.add(PopularItem(i.item, i.amount, a.id, r.id, i.value))
                }
            }

            userActionRepository.findMostRated(now.minus(r.period), 100).forEach { i ->
                popularItemsRepository.add(PopularItem(i.item, i.amount, ActionType.RATING.id, r.id, i.value))
            }
        }
    }
}