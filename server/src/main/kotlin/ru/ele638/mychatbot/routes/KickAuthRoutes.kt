package ru.ele638.mychatbot.routes

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.KickRequestToken
import ru.ele638.mychatbot.app.data.network.dto.KickStartAuthRequest
import ru.ele638.mychatbot.app.data.network.dto.TokenResponse
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.repository.KickSessionRepositoryImpl
import ru.ele638.mychatbot.repository.UserConfigRepository
import ru.ele638.mychatbot.repository.UserRepository
import ru.ele638.mychatbot.utils.CodeChallenge
import ru.ele638.mychatbot.utils.CodeChallengeGenerator
import kotlin.random.Random

fun Routing.oauthRoutes() {
    val baseKickAuthUr = "https://id.kick.com/oauth"


    authenticate {
        get("/oauth/start") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val username =
                principal.payload.getClaim("username")?.asString() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized
                )

            val request: KickStartAuthRequest = call.receive()
            val userConfigRepository: UserConfigRepository by inject()
            val result = userConfigRepository.updateKickSecrets(
                username,
                request.clientId,
                request.clientSecret
            )
            if (!result) {
                call.respond(status = HttpStatusCode.BadRequest, "Unable to save user config")
                return@get
            }

            val codeChallengeGenerator: CodeChallengeGenerator by inject()
            val codeChallenge = codeChallengeGenerator.generatePair()
            val kickSessionRepository: KickSessionRepository by inject()
            if (!kickSessionRepository.createSession(
                    ConnectKickSession(
                        userName = username,
                        codeChallenge = CodeChallenge(
                            codeChallenge = codeChallenge.codeChallenge,
                            codeVerifier = codeChallenge.codeVerifier
                        ),
                        appCallbackUri = request.appCallbackUri
                    )
                )
            ) {
                call.respond(status = HttpStatusCode.BadRequest, "Unable to save new kick session")
                return@get
            }


            val kickAuthUrl = baseKickAuthUr +
                    "/authorize?" +
                    "response_type=code&" +
                    "client_id=${request.clientId}&" +
                    "redirect_uri=${request.appCallbackUri}&" +
                    "scope=${request.scopes.joinToString("+") { it.stringKey }}&" +
                    "code_challenge=${codeChallenge.codeChallenge}&" +
                    "code_challenge_method=S256&" +
                    "state=${Random.nextInt()}"

            call.respond(status = HttpStatusCode.OK, kickAuthUrl)
        }
    }
    authenticate {
        get("/oauth/requestToken") {
            val principal = call.principal<JWTPrincipal>()
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val username =
                principal.payload.getClaim("username")?.asString() ?: return@get call.respond(
                    HttpStatusCode.Unauthorized
                )

            val request: KickRequestToken = call.receive()
            val userRepository: UserRepository by inject()
            val user = userRepository.getUser(username)
            if (user?.clientId == null || user.clientSecret == null) {
                return@get call.respond(
                    status = HttpStatusCode.InternalServerError,
                    "No client data"
                )
            }
            val kickSessionRepository: KickSessionRepositoryImpl by inject()
            val session = kickSessionRepository.getSession(username)
                ?: return@get call.respond(HttpStatusCode.Unauthorized)
            val tokenRequestClient = HttpClient {
                install(ContentNegotiation) { json() }
            }

            val tokenResponse: TokenResponse = tokenRequestClient.post("$baseKickAuthUr/token") {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody(
                    FormDataContent(Parameters.build {
                        append("code", request.permissionCode)
                        append("client_id", user.clientId)
                        append("client_secret", user.clientSecret)
                        append("redirect_uri", session.appCallbackUri)
                        append("grant_type", "authorization_code")
                        append("code_verifier", session.codeChallenge.codeVerifier)
                    })
                )
            }.body()


        }
    }
}