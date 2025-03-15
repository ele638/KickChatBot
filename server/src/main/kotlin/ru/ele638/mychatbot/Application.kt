package ru.ele638.mychatbot

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import ru.ele638.mychatbot.app.data.Platform
import ru.ele638.mychatbot.di.databaseModule
import ru.ele638.mychatbot.di.sharedModule
import ru.ele638.mychatbot.repository.UserRepository

const val SERVER_PORT = 8123

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(Koin) {
        modules(sharedModule, databaseModule)
    }

    val platform: Platform by inject()
    val userRepository: UserRepository by inject()
    routing {
        get("/") {
            call.respondText("Ktor: ${platform.name}")
        }
        post("/users") {
            val request = call.receive<Map<String, String>>()
            val id = userRepository.addUser(request["name"] ?: "Unknown")
            call.respond(HttpStatusCode.Created, "User added with ID: $id")
        }

        get("/users") {
            call.respond(userRepository.getUsers())
        }
    }
}