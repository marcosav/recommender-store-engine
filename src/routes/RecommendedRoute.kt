package com.gmail.marcosav2010.routes

import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*


@KtorExperimentalLocationsAPI
fun Route.recommended() {

    get<RecommendedPath> {
        call.respond(emptyList<Long>())
    }
}


@KtorExperimentalLocationsAPI
@Location("/recommended")
data class RecommendedPath(
    val user: Long
)