package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.UserActionService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


@KtorExperimentalLocationsAPI
fun Route.itemStats() {

    val userActionService by closestDI().instance<UserActionService>()

    get<ItemStatsPath> {
        val visits = userActionService.findVisitAmount(it.item)
        val rating = userActionService.findAverageRating(it.item) ?: 0.0
        call.respond(ItemStats(visits, rating))
    }
}

@KtorExperimentalLocationsAPI
@Location("/stats")
data class ItemStatsPath(
    val item: Long
)

data class ItemStats(val visits: Long, val rating: Double)