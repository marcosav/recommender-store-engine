package com.gmail.marcosav2010

import com.gmail.marcosav2010.routes.clientCollectorRoutes
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
    private val CLIENT_COLLECTOR_PORT = envPort(Constants.CLIENT_COLLECTOR_PORT, 8080)
    private val RECOMMENDER_PORT = envPort(Constants.RECOMMENDER_PORT, 8081)

    @KtorExperimentalLocationsAPI
    fun start(di: DI) {
        createServer(CLIENT_COLLECTOR_PORT, di, false) {
            install(CORS) {
                header(HttpHeaders.Authorization)

                configureHosts()
            }

            install(Authentication) { setupJWT() }

            clientCollectorRoutes()
        }

        createServer(RECOMMENDER_PORT, di) {
            recommenderRoutes()
        }
    }

    private fun createServer(port: Int, di: DI, wait: Boolean = true, config: Application.() -> Unit) {
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

    private fun envPort(env: String, default: Int): Int =
        runCatching { System.getenv(env).toIntOrNull() }.getOrNull() ?: default
}