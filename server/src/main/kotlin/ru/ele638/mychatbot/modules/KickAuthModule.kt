package ru.ele638.mychatbot.modules

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.app.data.network.dto.KickStartAuthRequest
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.data.network.TokenResponse
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.repository.UserRepository
import ru.ele638.mychatbot.utils.getUsernameFromJWT
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation as ClientContentNegotiation

fun kickOauthRoutes(application: Application) = with(application) {
    val baseKickAuthUrl = "https://id.kick.com/oauth"

    val kickSessionRepository: KickSessionRepository by inject()
    val userRepository: UserRepository by inject()

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

                val session = kickSessionRepository.getPendingSession(username)
                    ?: return@get call.respond(
                        HttpStatusCode.InternalServerError,
                        ErrorResponse("No saved session!")
                    )
                val permissionCode = userRepository.getUser(username)?.kickPermissionCode ?: return@get call.respond(
                    HttpStatusCode.InternalServerError,
                    ErrorResponse("No saved permission code!")
                )
                val tokenRequestClient = HttpClient {
                    install(ClientContentNegotiation) {
                        json(Json {
                            prettyPrint = true
                            isLenient = true
                            ignoreUnknownKeys = true
                        })
                    }
                }

                try {
                    val tokenRequest = tokenRequestClient.post("$baseKickAuthUrl/token") {
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(
                            FormDataContent(Parameters.build {
                                append("code", permissionCode)
                                append("client_id", session.clientId)
                                append("client_secret", session.clientSecret)
                                append("redirect_uri", session.appCallbackUri)
                                append("grant_type", "authorization_code")
                                append("code_verifier", session.codeChallenge.codeVerifier)
                            })
                        )
                    }
                    val bodyText = tokenRequest.bodyAsText()
                    Napier.v { "Response from kick: status = ${tokenRequest.status}, body text = $bodyText" }
                    val tokenResponse: TokenResponse = tokenRequest.body()
                    if(tokenRequest.status == HttpStatusCode.OK) {
                        kickSessionRepository.updatePendingSession(username, ConnectKickSession.Status.COMPLETED)
                    }
                    call.respond(HttpStatusCode.OK, tokenResponse)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Error: $e"))
                }
            }
        }
    }
}