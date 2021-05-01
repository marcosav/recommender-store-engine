package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.services.UserActionService
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


@KtorExperimentalLocationsAPI
fun Route.userActions() {

    val userActionService by closestDI().instance<UserActionService>()

    get<UserRatingsPath> {
        val ratings = userActionService.findUserRatingsFor(it.user, it.item)
        call.respond(ratings)
    }
}

@KtorExperimentalLocationsAPI
@Location("/user/rating")
data class UserRatingsPath(
    val user: Long,
    val item: List<Long>
)