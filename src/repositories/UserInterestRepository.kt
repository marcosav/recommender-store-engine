package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.UserInterest
import com.mongodb.client.FindIterable
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.litote.kmongo.*

class UserInterestRepository(di: DI) : RepositoryBase<UserInterest>(di) {

    override val collection = database.getCollection<UserInterest>()

    fun addPredictedScore(user: Long, item: Long, predicted: Double) {
        if (collection.countDocuments(and(UserInterest::user eq user, UserInterest::item eq item)) > 0L)
            collection.updateOne(
                and(
                    UserInterest::user eq user,
                    UserInterest::item eq item
                ),
                setValue(UserInterest::predicted, predicted)
            ) else add(UserInterest(user, item, null, predicted))
    }

    fun findScoresFor(item: Long): FindIterable<UserInterest> = collection.find(UserInterest::item eq item)

    fun findUser(user: Long): FindIterable<UserInterest> = collection.find(UserInterest::user eq user)

    @Serializable
    data class User(val user: Long)

    fun findUsers() = collection.withDocumentClass<User>().find().projection(User::user).distinct()

    @Serializable
    data class Item(val item: Long)

    fun findItems() = collection.withDocumentClass<Item>().find().projection(Item::item).distinct()

    @Serializable
    data class AverageScore(val score: Double)

    fun findUserAverageScore(user: Long): Double? =
        collection.aggregate<AverageScore>(
            match(UserInterest::user eq user),
            group(UserInterest::user, UserInterest::score avg UserInterest::score),
        ).first()?.score

    fun findItemAverageScore(item: Long): Double? =
        collection.aggregate<AverageScore>(
            match(UserInterest::item eq item),
            group(UserInterest::item, UserInterest::score avg UserInterest::score),
        ).first()?.score

    fun findUserScoresFor(userId: Long, items: List<Long>): Map<Long, Double> = collection.find(
        and(
            UserInterest::user eq userId,
            UserInterest::item `in` items,
            UserInterest::score ne null
        )
    ).map { Pair(it.item, it.score!!) }.toMap()
}