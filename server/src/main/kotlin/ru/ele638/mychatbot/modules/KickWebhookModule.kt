package ru.ele638.mychatbot.modules

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.MessagesProcessor
import ru.ele638.mychatbot.app.data.network.dto.KickSubscribeEventsDTO
import ru.ele638.mychatbot.kickClient.KickClient
import ru.ele638.mychatbot.kickClient.models.events.EventTypeSerializer
import ru.ele638.mychatbot.kickClient.models.events.chat.ChatMessageSentEvent
import ru.ele638.mychatbot.utils.getUsernameFromJWT

fun kickWebhookModule(application: Application) = with(application) {
    val kickClient: KickClient by inject()
    val messagesProcessor: MessagesProcessor by inject()
    routing {
        authenticate {
            post("/kick/events/subscribe") {
                val username =
                    getUsernameFromJWT() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request: KickSubscribeEventsDTO = call.receive()

                try {
                    val events = request.events.map(EventTypeSerializer.Companion::findByEventType)
                    kickClient.subscribeToEvents(username, events)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }
        }

        authenticate {
            post("/kick/events/unsubscribe") {
                val username =
                    getUsernameFromJWT() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request: KickSubscribeEventsDTO = call.receive()

                try {
                    val events = request.events.map(EventTypeSerializer.Companion::findByEventType)
                    kickClient.unsubscribeFromEvents(username, events)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error: ${e.message}")
                }
            }
        }

        post("/webhook") {
            val eventName = call.request.headers["Kick-Event-Type"] ?: return@post call.respond(
                HttpStatusCode.OK
            )
            val event = EventTypeSerializer.findBySerializedName(eventName)
            val json = Json {
                ignoreUnknownKeys = true
            }
            val request = json.decodeFromJsonElement(event.serializer, call.receive())
            when (request) {
                is ChatMessageSentEvent -> messagesProcessor.processMessage(request)
            }
            call.respond(HttpStatusCode.OK)
        }
    }
}