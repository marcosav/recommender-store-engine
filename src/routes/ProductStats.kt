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

    get<ProductRating> {
        val rating = userActionService.findAverageRating(it.product)
        call.respond(rating)
    }

    get<ProductViews> {
        val views = userActionService.findClickAmount(it.product)
        call.respond(views)
    }
}


@KtorExperimentalLocationsAPI
@Location("/rating")
data class ProductRating(
    val product: Long
)

@KtorExperimentalLocationsAPI
@Location("/views")
data class ProductViews(
    val product: Long
)