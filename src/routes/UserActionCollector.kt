package com.gmail.marcosav2010.routes

import com.gmail.marcosav2010.Session
import com.gmail.marcosav2010.model.ActionType
import com.gmail.marcosav2010.model.UserAction
import com.gmail.marcosav2010.safeSession
import com.gmail.marcosav2010.services.UserActionService
import com.gmail.marcosav2010.session
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.util.pipeline.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit.MILLIS


@KtorExperimentalLocationsAPI
fun Route.clientCollector() =
    createCollectorRoute<ClientRecordUserAction>({ Pair(session.sessionId, session.userId) }) { it, _ ->
        ActionType.from(it.action)?.client != true
    }

@KtorExperimentalLocationsAPI
fun Route.serverCollector() =
    createCollectorRoute<RecordUserAction>({ Pair(it.sessionId, it.user) }) { it, userActionService ->
        if (it.action == ActionType.RATING.id && userActionService.hasActionFromUser(
                it.user,
                it.item,
                ActionType.RATING
            )
        ) userActionService.deleteLastRatingFor(it.user, it.item)

        false
    }

@KtorExperimentalLocationsAPI
private inline fun <reified T : IRecordRoute> Route.createCollectorRoute(
    crossinline sessionId: (PipelineContext<Unit, ApplicationCall>).(T) -> Pair<String, Long?>,
    crossinline check: (PipelineContext<Unit, ApplicationCall>).(T, UserActionService) -> Boolean = { _, _ -> false }
) {
    val userActionService by closestDI().instance<UserActionService>()

    post<T> {
        val a = ActionType.from(it.action) ?: return@post call.respond(HttpStatusCode.BadRequest)

        if (check(this, it, userActionService)) return@post call.respond(HttpStatusCode.BadRequest)

        val session = sessionId(this, it)
        if (it.action == ActionType.VISIT.id && userActionService.hasActionFromSession(
                session.first,
                it.item,
                ActionType.VISIT
            )
        ) return@post call.respond(HttpStatusCode.OK)

        if (a == ActionType.CLICK && session.second == null) return@post call.respond(HttpStatusCode.OK)

        var delta = -1L
        if (session.second != null) {
            val lastAction = userActionService.getLastAction(session.second!!)
            delta = lastAction?.date?.let { d -> MILLIS.between(d, LocalDateTime.now()) } ?: -1
        }

        val action = it.toAction(safeSession, delta)
        userActionService.add(action)

        call.respond(HttpStatusCode.OK)
    }
}

@KtorExperimentalLocationsAPI
@Location("/record")
data class ClientRecordUserAction(
    override val item: Long,
    override val action: Int,
    override val value: Double? = null
) : IRecordRoute {
    override fun toAction(session: Session?, delta: Long) =
        UserAction(session!!.sessionId, session.userId, item, action, LocalDateTime.now(), delta, value)
}

@KtorExperimentalLocationsAPI
@Location("/record")
data class RecordUserAction(
    val sessionId: String,
    val user: Long,
    override val item: Long,
    override val action: Int,
    override val value: Double? = null
) : IRecordRoute {
    override fun toAction(session: Session?, delta: Long) =
        UserAction(sessionId, user, item, action, LocalDateTime.now(), delta, value)
}

interface IRecordRoute {
    val item: Long
    val action: Int
    val value: Double?

    fun toAction(session: Session?, delta: Long): UserAction
}