package com.gmail.marcosav2010

import com.gmail.marcosav2010.services.recommender.RecommenderTask
import io.ktor.locations.*
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.kodein.di.DI
import java.util.concurrent.Executors

@KtorExperimentalLocationsAPI
suspend fun main() = coroutineScope {
    val di = DI {
        setupDatabase()
        setupRepositories()
        setupServices()
    }

    if (WebServer.ENGINE)
        launch(RECOMMENDER_DISPATCHER) {
            RecommenderTask(di).start()
        }

    WebServer.start(di)
}

private val RECOMMENDER_DISPATCHER get() = Executors.newSingleThreadExecutor().asCoroutineDispatcher()