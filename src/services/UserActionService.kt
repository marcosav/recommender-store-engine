package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance

class UserActionService(di: DI) {

    private val userActionRepository by di.instance<UserActionRepository>()

    fun add(action: UserAction) {
        userActionRepository.add(action)
    }

    fun findUserRatingsFor(userId: Long, items: List<Long>) = userActionRepository.findUserRatingsFor(userId, items)

    fun hasActionFromSession(sessionId: String, item: Long, action: ActionType) =
        userActionRepository.countActionsFromSession(sessionId, item, action) > 0

    fun hasActionFromUser(userId: Long, item: Long, action: ActionType) =
        userActionRepository.countActionsFromUser(userId, item, action) > 0

    fun findAverageRating(item: Long): Double? = userActionRepository.findAverageRating(item)

    fun findVisitAmount(item: Long): Long = userActionRepository.countActionsForItem(item, ActionType.VISIT)

    fun getLastAction(userId: Long): UserAction? = userActionRepository.getLastAction(userId)

    fun deleteLastRatingFor(userId: Long, item: Long) =
        userActionRepository.deleteLastAction(userId, item, ActionType.RATING)
}