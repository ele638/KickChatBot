package ru.ele638.mychatbot

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.modules.authModule
import ru.ele638.mychatbot.modules.initModule

private val BE_HOST = System.getenv("BE_HOST")
private val BE_PORT = System.getenv("BE_PORT").toInt()

fun main() {
    embeddedServer(Netty, port = BE_PORT, host = BE_HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    initModule()
    authModule()
    val platform: Platform by inject()
    routing {
        get("/") {
            call.respondText("Ktor: ${platform.name}")
        }
    }
}