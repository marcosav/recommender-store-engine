package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.mongodb.client.FindIterable
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.litote.kmongo.*

class UserActionRepository(di: DI) : RepositoryBase<UserAction>(di) {

    override val collection = database.getCollection<UserAction>()

    private fun getUserItemActionFilter(userId: Long, item: Long, action: ActionType) =
        and(
            UserAction::user eq userId,
            UserAction::item eq item,
            UserAction::action eq action.id
        )

    fun add(action: UserAction) {
        collection.insertOne(action)
    }

    fun findByUser(userId: Long): FindIterable<UserAction> = collection.find(UserAction::user eq userId)

    fun findByUserAndItem(userId: Long, item: Long): FindIterable<UserAction> =
        collection.find(and(UserAction::user eq userId, UserAction::item eq item))

    fun findUserRatingsFor(userId: Long, items: List<Long>): Map<Long, Double> = collection.find(
        and(
            UserAction::user eq userId,
            UserAction::item `in` items,
            UserAction::action eq ActionType.RATING.id
        )
    ).map { Pair(it.item, it.value!!) }.toMap()

    @Serializable
    data class AverageRating(val value: Double)

    fun findAverageRating(item: Long): Double? =
        collection.aggregate<AverageRating>(
            match(and(UserAction::item eq item, UserAction::action eq ActionType.RATING.id)),
            group(UserAction::item, UserAction::value avg UserAction::value),
        ).first()?.value

    fun countActionsForItem(item: Long, action: ActionType): Long =
        collection.countDocuments(and(UserAction::item eq item, UserAction::action eq action.id))

    fun countActionsFromSession(sessionId: String, item: Long, action: ActionType): Long =
        collection.countDocuments(
            and(
                UserAction::session eq sessionId,
                UserAction::item eq item,
                UserAction::action eq action.id
            )
        )

    fun countActionsFromUser(userId: Long, item: Long, action: ActionType): Long =
        collection.countDocuments(getUserItemActionFilter(userId, item, action))

    fun getLastAction(userId: Long): UserAction? =
        collection.find(UserAction::user eq userId).descendingSort(UserAction::date).first()

    fun deleteLastAction(userId: Long, item: Long, action: ActionType) {
        collection.deleteOne(getUserItemActionFilter(userId, item, action))
    }

    fun findRatingsFor(item: Long): FindIterable<UserAction> = collection.find(
        and(UserAction::item eq item, UserAction::action eq ActionType.RATING.id)
    )

    @Serializable
    data class User(val user: Long)

    fun findRaters() = collection.withDocumentClass<User>().find(
        and(UserAction::action eq ActionType.RATING.id)
    ).projection(UserAction::user).distinct()

    @Serializable
    data class UserActionItem(val item: Long)

    fun findRatedProducts() =
        collection.withDocumentClass<UserActionItem>().find(UserAction::action eq ActionType.RATING.id)
            .projection(UserActionItem::item).distinct()

    fun findUserAverageRating(user: Long): Double? =
        collection.aggregate<AverageRating>(
            match(UserAction::user eq user, UserAction::action eq ActionType.RATING.id),
            group(UserAction::user, UserAction::value avg UserAction::value),
        ).first()?.value

    @Serializable
    data class VisitedItem(val item: Long, val visits: Long)

    fun findMostVisited() = collection.aggregate<VisitedItem>(
        match(UserAction::action eq ActionType.VISIT.id),
        group(UserAction::item, VisitedItem::visits sum 1),
        project(VisitedItem::item from "\$_id", VisitedItem::visits from VisitedItem::visits)
    )
}