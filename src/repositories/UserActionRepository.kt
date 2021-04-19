package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.mongodb.client.FindIterable
import com.mongodb.client.result.InsertOneResult
import org.kodein.di.DI
import org.litote.kmongo.*

class UserActionRepository(di: DI) : RepositoryBase<UserAction>(di) {

    override val collection = database.getCollection<UserAction>()

    fun add(action: UserAction): InsertOneResult = collection.insertOne(action)

    fun findByUser(userId: Long): FindIterable<UserAction> = collection.find(UserAction::user eq userId)

    fun findByUserAndProduct(userId: Long, productId: Long): FindIterable<UserAction> =
        collection.find(and(UserAction::user eq userId, UserAction::product eq productId))

    fun findAverageRating(productId: Long): Double =
        collection.aggregate<Double>(
            match(and(UserAction::product eq productId, UserAction::action eq ActionType.RATING.id)),
            group("ratingAvg", UserAction::value avg UserAction::value)
        ).first() ?: 0.0

    fun findClickAmount(productId: Long): Long =
        collection.countDocuments(and(UserAction::product eq productId, UserAction::action eq ActionType.CLICK.id))
}