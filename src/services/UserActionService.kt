package com.gmail.marcosav2010.services

import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.repositories.UserActionRepository
import org.kodein.di.DI
import org.kodein.di.instance

class UserActionService(di: DI) {

    private val userActionRepository by di.instance<UserActionRepository>()

    fun add(action: UserAction) {
        userActionRepository.add(action)
    }

    fun findByUser(userId: Long) {
        userActionRepository.findByUser(userId)
    }

    fun findByUserAndProduct(userId: Long, productId: Long) {
        userActionRepository.findByUserAndProduct(userId, productId)
    }

    fun findAverageRating(productId: Long): Double = userActionRepository.findAverageRating(productId)

    fun findClickAmount(productId: Long): Long = userActionRepository.findClickAmount(productId)
}