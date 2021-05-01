package com.gmail.marcosav2010.repositories

import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.kodein.di.DI
import org.kodein.di.instance
import org.litote.kmongo.deleteMany

abstract class RepositoryBase<T : Any>(di: DI) {

    protected val database by di.instance<MongoDatabase>()

    protected abstract val collection: MongoCollection<T>

    fun add(entry: T) {
        collection.insertOne(entry)
    }

    fun clearAll() = collection.deleteMany().deletedCount
}