package ru.ele638.mychatbot

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import ru.ele638.mychatbot.di.sharedModule

const val SERVER_PORT = 8123

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(sharedModule)
    }
    val platform: Platform by inject<Platform>()
    routing {
        get("/") {
            call.respondText("Ktor: ${platform.name}")
        }
    }
}