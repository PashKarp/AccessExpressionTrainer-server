package com.example.plugins

import com.server.models.SessionObj
import io.ktor.server.sessions.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSecurity() {

    install(Sessions) {
        header<SessionObj>("MY_SESSION")
    }
    routing {
        get("/session/increment") {
                val session = call.sessions.get<SessionObj>() ?: SessionObj()
                call.sessions.set(session.copy(taskId = session.taskId + 1))
                call.respondText("Counter is ${session.taskId}. Refresh to increment.")
            }
    }
}
