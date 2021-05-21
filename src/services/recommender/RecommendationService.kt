package com.gmail.marcosav2010.services.recommender

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.RankType
import com.gmail.marcosav2010.repositories.PopularItemsRepository
import com.gmail.marcosav2010.repositories.SimilarItemRepository
import com.gmail.marcosav2010.repositories.UserTopItemsRepository
import org.kodein.di.DI
import org.kodein.di.instance

class RecommendationService(di: DI) {

    companion object {
        const val DEFAULT_ITEM_AMOUNT = 25
    }

    private val userTopItemsRepository by di.instance<UserTopItemsRepository>()
    private val similarItemRepository by di.instance<SimilarItemRepository>()
    private val popularItemsRepository by di.instance<PopularItemsRepository>()

    fun forUser(user: Long, amount: Int? = null): Iterable<Long> =
        userTopItemsRepository.findTopItems(user)?.sortedByDescending { it.score }?.take(amount ?: DEFAULT_ITEM_AMOUNT)
            ?.map { it.item }
            .orEmpty()

    fun forItem(item: Long, amount: Int? = null): Iterable<Long> =
        similarItemRepository.findSimilar(item)?.sortedByDescending { it.score }?.take(amount ?: DEFAULT_ITEM_AMOUNT)
            ?.map { it.item }
            .orEmpty()

    fun popular(rankType: RankType? = null, amount: Int? = null): Iterable<Long> =
        popularItemsRepository.forAction(ActionType.VISIT, rankType ?: RankType.MONTHLY)
            .take(amount ?: DEFAULT_ITEM_AMOUNT)
            .map { it.item }
}