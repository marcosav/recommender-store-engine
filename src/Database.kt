package com.gmail.marcosav2010

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoDatabase
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.litote.kmongo.KMongo

private val username = System.getenv(Constants.DATABASE_USERNAME)
private val password = System.getenv(Constants.DATABASE_PASSWORD)
private val database = System.getenv(Constants.DATABASE_NAME)
private val host = runCatching { System.getenv(Constants.DATABASE_HOST) }.getOrNull() ?: Constants.DEFAULT_HOST

private val createConnectionString get() = "mongodb://$username:$password@$host"

private fun createClient() = KMongo.createClient(createConnectionString)

fun DI.MainBuilder.setupDatabase() {
    bind<MongoClient>() with singleton { createClient() }
    bind<MongoDatabase>() with singleton { instance<MongoClient>().getDatabase(database) }
}