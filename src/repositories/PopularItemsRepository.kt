package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.PopularItem
import com.gmail.marcosav2010.model.PreliminaryEntry
import com.gmail.marcosav2010.model.RankType
import org.kodein.di.DI
import org.litote.kmongo.*

class PopularItemsRepository(di: DI) : PreliminaryEntryRepository<PopularItem>(di) {

    override val collection = database.getCollection<PopularItem>()

    fun forAction(action: ActionType, rank: RankType) =
        collection.find(
            and(
                PopularItem::action eq action.id,
                PopularItem::type eq rank.id,
                PreliminaryEntry::pre.exists(false)
            )
        ).let { l ->
            if (action == ActionType.RATING) l.sortedByDescending { it.value }
            else l.sortedByDescending { it.amount }
        }
}