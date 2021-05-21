package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.PreliminaryEntry
import com.gmail.marcosav2010.model.SimilarItems
import org.kodein.di.DI
import org.litote.kmongo.*

class SimilarItemRepository(di: DI) : PreliminaryEntryRepository<SimilarItems>(di) {

    override val collection = database.getCollection<SimilarItems>()

    fun findSimilar(item: Long) =
        collection.find(and(SimilarItems::item eq item, PreliminaryEntry::pre.exists(false))).firstOrNull()?.similar
}