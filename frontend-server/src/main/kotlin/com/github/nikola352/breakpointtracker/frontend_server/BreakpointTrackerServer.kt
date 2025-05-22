package com.github.nikola352.breakpointtracker.frontend_server

import com.github.nikola352.breakpointtracker.shared.BreakpointDto
import com.google.gson.Gson
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
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.seconds

class BreakpointTrackerServer {
    private val breakpointRepository = BreakpointRepository()
    private val connections = ConcurrentHashMap<String, DefaultWebSocketSession>()
    private val gson = Gson()

    fun updateBreakpoints(breakpointCount: Int, breakpoints: List<BreakpointDto>) {
        breakpointRepository.update(breakpointCount, breakpoints)
        notifyClients()
    }

    private val server = embeddedServer(
        CIO,
        port = 7159,
        host = "0.0.0.0",
        module = { module() }
    ).start(wait = false)

    private val port = server.environment.config.port

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
            allowMethod(HttpMethod.Options)
            allowHeader(HttpHeaders.ContentType)
        }
        install(WebSockets) {
            pingPeriod = 15.seconds
            timeout = 30.seconds
            maxFrameSize = Long.MAX_VALUE
            masking = false
        }

        routing {
            webSocket("/ws/breakpoints") {
                val sessionId = java.util.UUID.randomUUID().toString()
                connections[sessionId] = this

                try {
                    // Send initial data
                    val json = gson.toJson(breakpointRepository.breakpointData)
                    send(Frame.Text(json))

                    // Keep connection open
                    for (frame in incoming) {
                        // Only log incoming messages
                        if (frame is Frame.Text) {
                            val text = frame.readText()
                            log.info("Received message from client: $text")
                        }
                    }
                } catch (e: Exception) {
                    log.error("WebSocket error:", e)
                } finally {
                    connections.remove(sessionId)
                }
            }

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

    /** Notify all connected clients of breakpoint updates. */
    private fun notifyClients() {
        connections.values.forEach { session ->
            try {
                val json = gson.toJson(breakpointRepository.breakpointData)
                kotlinx.coroutines.runBlocking {
                    session.send(Frame.Text(json))
                }
            } catch (e: ClosedSendChannelException) {
                val connectionToRemove = connections.entries.find { it.value == session }
                connectionToRemove?.let { connections.remove(it.key) }
            } catch (e: Exception) {
                println("Error sending WebSocket message: ${e.message}")
            }
        }
    }
}
