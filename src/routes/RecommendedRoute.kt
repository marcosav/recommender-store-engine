package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.RankType
import com.gmail.marcosav2010.services.recommender.RecommendationService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI


@KtorExperimentalLocationsAPI
fun Route.recommended() {

    val recommendationService by closestDI().instance<RecommendationService>()

    get<RecommendedPath> {
        val r = when {
            it.item != null -> recommendationService.forItem(it.item, it.amount)
            it.user != null -> recommendationService.forUser(it.user, it.amount)
            else -> return@get call.respond(HttpStatusCode.BadRequest)
        }

        call.respond(r)
    }

    get<PopularPath> {
        val p = recommendationService.popular(RankType.from(it.period), it.amount)
        call.respond(p)
    }
}

@KtorExperimentalLocationsAPI
@Location("/recommended")
data class RecommendedPath(
    val user: Long? = null,
    val item: Long? = null,
    val amount: Int? = null
)

@KtorExperimentalLocationsAPI
@Location("/popular")
data class PopularPath(
    val period: Int = RankType.WEEKLY.id,
    val amount: Int?
)