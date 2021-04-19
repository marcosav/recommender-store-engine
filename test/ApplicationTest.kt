package com.gmail.marcosav2010

import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.locations.*
import kotlin.test.*
import io.ktor.server.testing.*
import module

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ module(testing = true) }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("HELLO WORLD!", response.content)
            }
        }
    }
}
