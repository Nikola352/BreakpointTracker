package com.github.nikola352.breakpointtracker.frontend_server

import com.github.nikola352.breakpointtracker.shared.BreakpointDto
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class BreakpointTrackerServer {
    private val breakpointRepository = BreakpointRepository()

    fun updateBreakpoints(breakpointCount: Int, breakpoints: List<BreakpointDto>) {
        breakpointRepository.update(breakpointCount, breakpoints)
    }

    private val server = embeddedServer(
        CIO,
        port = 8080,
        host = "0.0.0.0",
        module = { module() }
    ).start(wait = false)

    private val port = server.environment.config.port

    init {
        val resource = this::class.java.classLoader.getResource("static/index.html")
        println("Resource found: ${resource != null}")
    }

    val serverUrl get() = "http://localhost:$port"

    fun shutdown() {
        server.stop(1000, 2000)
    }

    private fun Application.module() {
        install(ContentNegotiation) {
            gson()
        }
        install(CORS) {
            anyHost()
            allowMethod(HttpMethod.Get)
            allowHeader(HttpHeaders.ContentType)
        }

        routing {
            get("/api/breakpoints") {
                call.respond(breakpointRepository.breakpointData)
            }

            get("/") {
                call.respondText(
                    this::class.java.classLoader.getResource("static/index.html")!!.readText(),
                    ContentType.Text.Html
                )
            }

            staticResources("/static", "static")
        }
    }
}
