package com.example.plugins

import com.server.controllers.ReasonerController
import com.server.controllers.TaskGenerationController
import com.server.models.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.plugins.autohead.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.sessions.*
import its.model.DomainModel

fun Application.configureRouting() {
    install(Resources)
    install(AutoHeadResponse)
    routing {
        get("/getTask/{id}") {
            val id = call.parameters["id"]!!.toInt()
            val session = call.sessions.get<SessionObj>() ?: SessionObj()
            val result = TaskGenerationController().getTask(id, session)
            call.sessions.set(result.second)
            call.respond(result.first)
        }
        get("/getNextTask/") {
            val session = call.sessions.get<SessionObj>() ?: SessionObj()
            val result = TaskGenerationController().getNextTask(session)
            call.sessions.set(result.second)
            call.respond(result.first)
        }
        post("/checkAnswer/") {
            val session = call.sessions.get<SessionObj>() ?: SessionObj()
            try {
                val element = call.receive<ClientAnswerElement>()
                call.respond(ReasonerController().checkAnswer(element, session))
            } catch (thrown: ContentTransformationException) {
                var excText = "exception"
                if (thrown.message != null)
                    excText = thrown.message.toString()
                call.respondText(excText);
            }
        }
        post("/checkFullAnswer/") {
            val session = call.sessions.get<SessionObj>() ?: SessionObj()
            try {
                val element = call.receive<ClientAnswerElement>()
                call.respond(ReasonerController().checkForComplete(element, session))
            } catch (thrown: ContentTransformationException) {
                var excText = "exception"
                if (thrown.message != null)
                    excText = thrown.message.toString()
                call.respondText(excText);
            }
        }
        post("/getHint/") {
            val session = call.sessions.get<SessionObj>() ?: SessionObj()
            try {
                val element = call.receive<ClientAnswerElement>()
                call.respond(ReasonerController().getHint(element, session))
            } catch (thrown: ContentTransformationException) {
                var excText = "exception"
                if (thrown.message != null)
                    excText = thrown.message.toString()
                call.respondText(excText);
            }
        }
    }
}
