package com.server

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import its.model.DomainModel
import its.model.dictionaries.*

fun main() {
    val dir = "F:\\files\\archive\\dip-server\\models\\input_examples_access\\"
//val dir = "inputs\\"

    DomainModel(
        ClassesDictionary(),
        DecisionTreeVarsDictionary(),
        EnumsDictionary(),
        PropertiesDictionary(),
        RelationshipsDictionary(),
        dir,
    )

    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureHTTP()
    configureSecurity()
    configureRouting()
}
