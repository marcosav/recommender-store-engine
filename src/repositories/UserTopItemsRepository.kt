package com.gmail.marcosav2010.repositories

import com.gmail.marcosav2010.model.UserTopItems
import org.kodein.di.DI
import org.litote.kmongo.eq
import org.litote.kmongo.getCollection

class UserTopItemsRepository(di: DI) : RepositoryBase<UserTopItems>(di) {

    override val collection = database.getCollection<UserTopItems>()

    fun findTopItems(user: Long) = collection.find(UserTopItems::user eq user).firstOrNull()?.items
}