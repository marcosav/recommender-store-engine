package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.routing.*

const val COLLECTOR_BASE_ROUTE = "/v${Constants.COLLECTOR_API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.collectorRoutes() {

    routing {
        route(COLLECTOR_BASE_ROUTE) {
            authenticate {
                collector()
            }
        }
    }
}

const val RECOMMENDER_BASE_ROUTE = "/v${Constants.RECOMMENDER_API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.recommenderRoutes() {
    routing {
        route(RECOMMENDER_BASE_ROUTE) {
            productStats()
            recommended()
        }
    }
}