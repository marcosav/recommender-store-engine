package com.gmail.marcosav2010

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.util.pipeline.*
import java.time.Clock
import java.util.*

inline val PipelineContext<*, ApplicationCall>.session: Session get() = call.authentication.principal()!!

private val clock = Clock.systemUTC()

private val jwtAlgorithm = Algorithm.HMAC384(System.getenv(Constants.JWT_SECRET_ENV))

val jwtVerifier: JWTVerifier =
    (JWT.require(jwtAlgorithm) as JWTVerifier.BaseVerification).build { Date(clock.millis()) }

fun Authentication.Configuration.setupJWT() {
    jwt {
        verifier(jwtVerifier)
        realm = "mav"

        validate {
            val sessionId = it.payload.subject
            val userId = it.payload.claims[Constants.USER_ID_CLAIM]?.asLong()

            userId?.let { Session(sessionId, userId) }
        }
    }
}

data class Session(val sessionId: String, val userId: Long) : Principal