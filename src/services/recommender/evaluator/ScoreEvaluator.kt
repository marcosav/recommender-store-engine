package com.gmail.marcosav2010.services.recommender.evaluator

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserInterest
import com.gmail.marcosav2010.repositories.UserActionRepository
import com.gmail.marcosav2010.repositories.UserInterestRepository
import org.kodein.di.DI
import org.kodein.di.instance
import java.time.LocalDateTime
import java.time.Period

class ScoreEvaluator(di: DI) {

    private val userActionRepository by di.instance<UserActionRepository>()
    private val userInterestRepository by di.instance<UserInterestRepository>()

    private val since = LocalDateTime.now().minus(Period.ofMonths(1))

    fun execute() {
        userInterestRepository.clearAll()

        val r = userActionRepository.findEvaluableItemsAndUsers()!!
        r.users.forEach { user ->
            r.items.forEach { item ->
                val entry = calculateInterest(user, item, since)
                if (entry != null) userInterestRepository.add(entry)
            }
        }
    }

    private fun calculateInterest(u: Long, i: Long, since: LocalDateTime): UserInterest? {
        val actions = userActionRepository.findByUserAndItem(u, i, since)
        // TODO: Evaluator
        val rating = actions.find { it.action == ActionType.RATING.id }?.value
        val score = rating ?: return null
        return UserInterest(u, i, score)
    }
}