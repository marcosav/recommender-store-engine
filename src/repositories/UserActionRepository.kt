package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.mongodb.client.FindIterable
import kotlinx.serialization.Serializable
import org.kodein.di.DI
import org.litote.kmongo.*
import java.time.LocalDateTime

class UserActionRepository(di: DI) : RepositoryBase<UserAction>(di) {

    override val collection = database.getCollection<UserAction>()

    private fun getUserItemActionFilter(userId: Long, item: Long, action: ActionType) =
        and(
            UserAction::user eq userId,
            UserAction::item eq item,
            UserAction::action eq action.id
        )

    @Serializable
    data class ItemsAndUsers(val items: List<Long>, val users: List<Long>)

    fun findEvaluableItemsAndUsers(): ItemsAndUsers? = collection.aggregate<ItemsAndUsers>(
        match(UserAction::action ne ActionType.VISIT.id),
        group(
            null,
            ItemsAndUsers::items addToSet UserAction::item,
            ItemsAndUsers::users addToSet UserAction::user
        ),
    ).firstOrNull()

    fun findByUserAndItem(userId: Long, item: Long, since: LocalDateTime): FindIterable<UserAction> =
        collection.find(
            and(
                UserAction::action ne ActionType.VISIT.id,
                UserAction::user eq userId,
                UserAction::item eq item,
                UserAction::date gte since
            )
        )

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

    @Serializable
    data class RankedItem(val item: Long, val amount: Long, val value: Double?)

    fun findMost(action: ActionType, since: LocalDateTime, limit: Int) = collection.aggregate<RankedItem>(
        match(UserAction::action eq action.id, UserAction::date gte since),
        group(UserAction::item, RankedItem::amount sum 1),
        project(
            RankedItem::item from "\$_id",
            RankedItem::amount from RankedItem::amount
        ),
        sort(descending(RankedItem::amount)),
        limit(limit)
    )

    fun findMostRated(since: LocalDateTime, limit: Int) = collection.aggregate<RankedItem>(
        match(UserAction::action eq ActionType.RATING.id, UserAction::date gte since),
        group(UserAction::item, RankedItem::amount sum 1, UserAction::value avg UserAction::value),
        project(
            RankedItem::item from "\$_id",
            RankedItem::amount from RankedItem::amount,
            RankedItem::value from UserAction::value
        ),
        sort(descending(RankedItem::value)),
        limit(limit)
    )
}