package com.gmail.marcosav2010.auth

import com.gmail.marcosav2010.Constants
import io.ktor.auth.*
import io.ktor.util.*

private val username = getCredential(Constants.ENGINE_API_AUTH_USER)
private val password = getCredential(Constants.ENGINE_API_AUTH_PASSWORD)

private const val SALT = "r-engine"
private val sha256 = getDigestFunction("SHA-256") { "$SALT-${it.length}" }

private val hashedCredentials = UserHashedTableAuth(
    table = mapOf(username to sha256(password)),
    digester = sha256
)

fun Authentication.Configuration.setupEngineAuth() {
    basic {
        realm = "mav"
        validate { credentials ->
            hashedCredentials.authenticate(credentials)
        }
    }
}

private fun getCredential(env: String) =
    runCatching { System.getenv(env) }.getOrElse { throw IllegalArgumentException("Please, set $env env variable") }