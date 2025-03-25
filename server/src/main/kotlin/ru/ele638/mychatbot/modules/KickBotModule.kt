package ru.ele638.mychatbot.modules

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.KickBotPostMessageRequest
import ru.ele638.mychatbot.kickClient.KickClient
import ru.ele638.mychatbot.utils.getUsernameFromJWT

fun kickBotModule(application: Application) = with(application) {
    val kickClient: KickClient by inject()

    routing {
        authenticate {
            post("/kick/bot/postMessage") {
                val username = getUsernameFromJWT()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<KickBotPostMessageRequest>()

                if (request.content.length > 500) {
                    return@post call.respond(HttpStatusCode.BadRequest, "Content is too long")
                }
                try {
                    kickClient.postMessageIntoChat(username, request.content)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }
        }
    }
}