package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Constants
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.routing.*

const val FEEDBACK_BASE_ROUTE = "/v${Constants.FEEDBACK_API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.clientFeedbackRoutes() {
    routing {
        route(FEEDBACK_BASE_ROUTE) {
            authenticate {
                clientFeedback()
            }
        }
    }
}

const val RECOMMENDER_BASE_ROUTE = "/v${Constants.RECOMMENDER_API_VERSION}"

@KtorExperimentalLocationsAPI
fun Application.recommenderRoutes() {
    routing {
        route(RECOMMENDER_BASE_ROUTE) {
            authenticate {
                itemStats()
                userActions()
                recommended()
                serverFeedback()
            }
        }
    }
}