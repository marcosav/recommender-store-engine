package com.gmail.marcosav2010.services.recommender.popular

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.PopularItem
import com.gmail.marcosav2010.model.RankType
import com.gmail.marcosav2010.repositories.PopularItemsRepository
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDateTime
import java.time.Period

class PopularityRanker(di: DI) {

    companion object {
        val ACTION_TYPES = listOf(ActionType.VISIT)
        const val AMOUNT = 100
    }

    private val popularItemsRepository by di.instance<PopularItemsRepository>()
    private val userActionRepository by di.instance<UserActionRepository>()

    fun execute() {
        popularItemsRepository.cleanMarked()

        val now = LocalDateTime.now()

        RankType.values().forEach { r ->
            val since = now.minus(r.period)

            ACTION_TYPES.forEach { a ->
                userActionRepository.findMost(a, since, AMOUNT).forEach { i ->
                    popularItemsRepository.add(PopularItem(i.item, i.amount, a.id, r.id))
                }
            }

            userActionRepository.findMostRated(since, AMOUNT).forEach { i ->
                popularItemsRepository.add(PopularItem(i.item, i.amount, ActionType.RATING.id, r.id, i.value))
            }
        }

        popularItemsRepository.clean()
    }
}