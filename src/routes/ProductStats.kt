package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.UserActionService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


@KtorExperimentalLocationsAPI
fun Route.productStats() {

    val userActionService by closestDI().instance<UserActionService>()

    get<ProductStatsPath> {
        val visits = userActionService.findVisitAmount(it.product)
        val rating = userActionService.findAverageRating(it.product) ?: 0.0
        call.respond(ProductStats(visits, rating))
    }
}

@KtorExperimentalLocationsAPI
@Location("/stats")
data class ProductStatsPath(
    val product: Long
)

data class ProductStats(val visits: Long, val rating: Double)