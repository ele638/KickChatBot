package ru.ele638.mychatbot.modules

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.ConfigPermissionUpdateRequest
import ru.ele638.mychatbot.app.data.network.dto.ConfigUpdateRequest
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.kickClient.KickClient
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.repository.UserRepository
import ru.ele638.mychatbot.utils.getUsernameFromJWT

fun configModule(application: Application) = with(application) {

    val userRepository by inject<UserRepository>()
    val kickSessionRepository by inject<KickSessionRepository>()
    val kickClient by inject<KickClient>()

    routing {
        authenticate {
            post("/config/kick/updateSecrets") {
                val userName =
                    getUsernameFromJWT() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<ConfigUpdateRequest>()
                if (userRepository.updateUserKickConfig(
                        username = userName,
                        kickClientId = request.clientId,
                        kickClientSecret = request.clientSecret
                    )
                ) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Unable to save config")
                    )
                }
            }
        }

        authenticate {
            post("/config/kick/updatePermissionCode") {
                val userName =
                    getUsernameFromJWT() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<ConfigPermissionUpdateRequest>()
                if (request.permissionCode.isBlank()) {
                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Permission code is empty!")
                    )
                }
                if (!userRepository.updateUserKickPermissionCode(
                        username = userName,
                        kickPermissionCode = request.permissionCode
                    )
                ) {
                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Unable to save permission code")
                    )
                }
                if (!kickSessionRepository.updatePendingSession(userName, ConnectKickSession.Status.WAITING_TOKEN)) {
                    return@post call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("Unable to update session")
                    )
                }
                call.respond(HttpStatusCode.OK)
            }
        }

        authenticate {
            post("/config/kick/updateBroadcasterId") {
                val userName =
                    getUsernameFromJWT() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                try {
                    val broadcasterId: Int = kickClient.getBroadcasterId(userName)
                    userRepository.updateKickBroadcasterId(userName, broadcasterId)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error ${e.message}")
                }
            }
        }
    }
}