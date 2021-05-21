package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.PreliminaryEntry
import com.gmail.marcosav2010.model.UserTopItems
import org.kodein.di.DI
import org.litote.kmongo.*

class UserTopItemsRepository(di: DI) : PreliminaryEntryRepository<UserTopItems>(di) {

    override val collection = database.getCollection<UserTopItems>()

    fun findTopItems(user: Long) =
        collection.find(and(UserTopItems::user eq user, PreliminaryEntry::pre.exists(false))).firstOrNull()?.items
}