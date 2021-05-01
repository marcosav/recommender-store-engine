package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.PopularItem
import com.gmail.marcosav2010.model.RankType
import org.kodein.di.DI
import org.litote.kmongo.and
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class PopularItemsRepository(di: DI) : RepositoryBase<PopularItem>(di) {

    override val collection = database.getCollection<PopularItem>()

    fun forAction(action: ActionType, rank: RankType) =
        collection.find(and(PopularItem::action eq action.id, PopularItem::type eq rank.id))
            .sortedByDescending { it.amount }
}