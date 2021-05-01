package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.SimilarItems
import org.kodein.di.DI
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class SimilarItemRepository(di: DI) : RepositoryBase<SimilarItems>(di) {

    override val collection = database.getCollection<SimilarItems>()

    fun findSimilar(item: Long) = collection.find(SimilarItems::item eq item).firstOrNull()?.similar
}