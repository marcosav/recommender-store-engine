package com.gmail.marcosav2010

import com.gmail.marcosav2010.auth.setupClientFeedbackAuth
import com.gmail.marcosav2010.auth.setupEngineAuth
import com.gmail.marcosav2010.routes.clientFeedbackRoutes
import com.gmail.marcosav2010.routes.recommenderRoutes
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.kodein.di.DI
import org.kodein.di.ktor.di

object WebServer {
    private val CLIENT_FEEDBACK_PORT = envPort(Constants.CLIENT_FEEDBACK_PORT)
    private val ENGINE_PORT = envPort(Constants.ENGINE_PORT)

    val ENGINE = ENGINE_PORT != null

    @KtorExperimentalLocationsAPI
    fun start(di: DI) {
        createServer(CLIENT_FEEDBACK_PORT, di, false) {
            install(CORS) {
                header(HttpHeaders.Authorization)

                configureHosts()
            }

            install(Authentication) { setupClientFeedbackAuth() }

            clientFeedbackRoutes()
        }

        createServer(ENGINE_PORT, di) {
            install(Authentication) { setupEngineAuth() }

            recommenderRoutes()
        }
    }

    private fun createServer(port: Int?, di: DI, wait: Boolean = true, config: Application.() -> Unit) =
        port?.let {
            embeddedServer(Netty, port = port) {
                install(ContentNegotiation) { gson {} }
                install(Locations)
                install(Compression) { gzip {}; deflate {} }

                di { extend(di) }

                config(this)
            }.start(wait)
        }

    private fun CORS.Configuration.configureHosts() =
        kotlin.runCatching { System.getenv(Constants.ALLOWED_HOSTS_ENV) }.getOrNull()?.let {
            it.split(",").filter { h -> h.isNotBlank() }
                .forEach { h -> if (h.startsWith("localhost")) host(h) else host(h, listOf("https")) }
        }

    private fun envPort(env: String): Int? = runCatching { System.getenv(env).toIntOrNull() }.getOrNull()
}