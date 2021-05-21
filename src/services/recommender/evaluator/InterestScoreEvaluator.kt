package com.gmail.marcosav2010.services.recommender.evaluator

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserInterest
import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.repositories.UserInterestRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDateTime
import java.time.Period
import kotlin.math.min

class InterestScoreEvaluator(di: DI) {

    private val userActionRepository by di.instance<UserActionRepository>()
    private val userInterestRepository by di.instance<UserInterestRepository>()

    private val startTime = System.currentTimeMillis()

    private val since = LocalDateTime.now().minus(Period.ofMonths(1))

    fun execute(): Boolean {
        userInterestRepository.deleteAll()

        val r = userActionRepository.findEvaluableItemsAndUsers() ?: return false
        r.users.forEach { user ->
            r.items.forEach { item ->
                val entry = calculateInterest(user, item, since)
                if (entry != null) userInterestRepository.add(entry)
            }
        }

        printElapsed()

        return true
    }

    private fun calculateInterest(u: Long, i: Long, since: LocalDateTime): UserInterest? {
        val actions = userActionRepository.findByUserAndItem(u, i, since)

        val rating = actions.find { it.action == ActionType.RATING.id }?.value ?: 0.0
        val clicks = actions.count { it.action == ActionType.CLICK.id }
        val addedToCart = actions.any { it.action == ActionType.CART.id }
        val bought = actions.any { it.action == ActionType.BUY.id }
        val favorite = actions.any { it.action == ActionType.FAVORITE.id }

        val score = rating * 0.2 +
                min(clicks * 0.1, 1.0) +
                (if (addedToCart) 1.0 else 0.0) +
                (if (bought) 1.0 else 0.0) +
                (if (favorite) 1.0 else 0.0)

        if (score == 0.0) return null

        return UserInterest(u, i, min(5.0, score))
    }

    private fun printElapsed() = println("\nScore calculator ${System.currentTimeMillis() - startTime} ms")
}