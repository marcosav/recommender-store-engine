package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.services.UserActionService
import com.gmail.marcosav2010.session
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.time.LocalDateTime


@KtorExperimentalLocationsAPI
fun Route.collector() {

    val userActionService by closestDI().instance<UserActionService>()

    post<RecordUserAction> {
        val action = it.toAction(session.userId)
        userActionService.add(action)

        call.respond(HttpStatusCode.OK)
    }
}

@KtorExperimentalLocationsAPI
@Location("/record")
data class RecordUserAction(
    val product: Long,
    val action: Int,
    val value: Int? = null
) {
    fun toAction(user: Long) = UserAction(user, product, action, LocalDateTime.now(), 0L)
}