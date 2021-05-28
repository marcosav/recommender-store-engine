package com.gmail.marcosav2010.services.recommender.evaluator

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.model.UserInterest
import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.repositories.UserInterestRepository
import org.kodein.di.DI
import org.kodein.di.instance
import org.litote.kmongo.ascendingSort
import java.time.LocalDateTime
import java.time.Period
import kotlin.math.min

class InterestScoreEvaluator(di: DI) {

    companion object {
        const val MAX_RATING = 5.0
        const val MAX_CLICK_RATING = 1.5
        const val CLICK_FACTOR = 0.1
        const val CART_RATING = 1.0
        const val FAVORITE_RATING = 1.0
        const val BUY_RATING = 1.25
        const val RATE_FACTOR = 0.25
    }

    private val userActionRepository by di.instance<UserActionRepository>()
    private val userInterestRepository by di.instance<UserInterestRepository>()

    private val startTime = System.currentTimeMillis()

    private val since = LocalDateTime.now().minus(Period.ofMonths(1))

    fun execute(): Boolean {
        userInterestRepository.deleteAll()

        val r = userActionRepository.findEvaluableItemsAndUsers() ?: return false
        r.users.forEach { user ->
            r.items.forEach { item ->
                val entry = getUserInterest(user, item, since)
                if (entry != null) userInterestRepository.add(entry)
            }
        }

        printElapsed()

        return true
    }

    private fun getUserInterest(u: Long, i: Long, since: LocalDateTime): UserInterest? {
        val actions = userActionRepository.findByUserAndItem(u, i, since)

        val score = calculateInterest(actions)

        if (score == 0.0) return null

        return UserInterest(u, i, score)
    }

    internal fun calculateInterest(actions: Iterable<UserAction>): Double {
        val rating = actions.find { it.action == ActionType.RATING.id }?.value ?: 0.0
        val clicks = actions.count { it.action == ActionType.CLICK.id }
        val addedToCart = actions.any { it.action == ActionType.CART.id }
        val bought = actions.any { it.action == ActionType.BUY.id }
        val favorite = actions.any { it.action == ActionType.FAVORITE.id }

        return min(
            MAX_RATING, rating * RATE_FACTOR +
                    min(clicks * CLICK_FACTOR, MAX_CLICK_RATING) +
                    (if (addedToCart) CART_RATING else 0.0) +
                    (if (bought) BUY_RATING else 0.0) +
                    (if (favorite) FAVORITE_RATING else 0.0)
        )
    }

    private fun printElapsed() = println("\nScore calculator ${System.currentTimeMillis() - startTime} ms")
}