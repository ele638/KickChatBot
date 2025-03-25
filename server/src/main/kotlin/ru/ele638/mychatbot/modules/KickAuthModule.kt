package ru.ele638.mychatbot.modules

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.app.data.network.dto.KickStartAuthRequest
import ru.ele638.mychatbot.kickClient.KickTokenManager
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.utils.getUsernameFromJWT

fun kickOauthRoutes(application: Application) = with(application) {
    val baseKickAuthUrl = "https://id.kick.com/oauth"

    val kickSessionRepository: KickSessionRepository by inject()
    val kickTokenManager: KickTokenManager by inject()

    routing {
        authenticate {
            get("/oauth/kick/start") {
                val username = getUsernameFromJWT() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized
                )

                val request: KickStartAuthRequest = call.receive()

                val session = kickSessionRepository.createSession(
                    username = username,
                    requestAppCallbackUri = request.appCallbackUri,
                    requestScopes = request.scopes
                )
                if (session == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        "Unable to save new kick session"
                    )
                    return@get
                }

                call.respond(status = HttpStatusCode.OK, session.buildUrl(baseKickAuthUrl))
            }
        }

        authenticate {
            get("/oauth/kick/requestToken") {
                val username = getUsernameFromJWT() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized
                )

                try {
                    kickTokenManager.requestAndSaveToken(username)
                    call.respond(HttpStatusCode.OK)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error: $e"))
                }
            }
        }
    }
}