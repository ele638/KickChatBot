package ru.ele638.mychatbot.kickClient

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.ele638.mychatbot.data.ConnectKickSession
import ru.ele638.mychatbot.data.KickToken
import ru.ele638.mychatbot.kickClient.models.responses.TokenResponse
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.repository.KickTokenRepository
import ru.ele638.mychatbot.repository.UserRepository

interface KickTokenManager {
    suspend fun requestAndSaveToken(username: String)
    suspend fun refreshAndSaveToken(username: String)
    fun getSavedToken(username: String): KickToken?
}

class KickTokenManagerImpl(
    private val kickTokenRepository: KickTokenRepository,
    private val userRepository: UserRepository,
    private val kickSessionRepository: KickSessionRepository
) : KickTokenManager {
    private val baseKickAuthUrl = "https://id.kick.com/oauth"

    private val authHttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    override suspend fun requestAndSaveToken(username: String) {
        val session = kickSessionRepository.getPendingSession(username)
            ?: throw IllegalStateException("No pending session!")
        val permissionCode = userRepository.getUser(username)?.kickPermissionCode
            ?: throw IllegalStateException("No permission code!")
        val request = authHttpClient.post("$baseKickAuthUrl/token") {
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
        if (request.status != HttpStatusCode.OK) {
            throw IllegalStateException("Token request failed! Status: ${request.status}, body: ${request.bodyAsText()}")
        }
        if (!kickSessionRepository.updatePendingSession(
                username,
                ConnectKickSession.Status.COMPLETED
            )
        ) {
            throw IllegalStateException("Unable to finish pending session!")
        }
        val tokenDto = request.body<TokenResponse>()
        kickTokenRepository.saveToken(
            username = username,
            newAuthToken = tokenDto.accessToken,
            newRefreshToken = tokenDto.refreshToken,
            expiresIn = tokenDto.expiresIn
        ) ?: throw IllegalStateException("Unable to save token!")
    }

    override suspend fun refreshAndSaveToken(username: String) {
        val user = userRepository.getUser(username)

        requireNotNull(user) { "No user!" }
        requireNotNull(user.kickClientId) { "No client id!" }
        requireNotNull(user.kickClientSecret) { "No client secret!" }

        val savedToken = kickTokenRepository.getSavedToken(username)
        requireNotNull(savedToken) { "No saved token!" }

        val request = authHttpClient.post("$baseKickAuthUrl/token") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(
                FormDataContent(Parameters.build {
                    append("client_id", user.kickClientId)
                    append("client_secret", user.kickClientSecret)
                    append("grant_type", "refresh_token")
                    append("refresh_token", savedToken.refreshToken)
                })
            )
        }
        val tokenDto = request.body<TokenResponse>()
        kickTokenRepository.saveToken(
            username = username,
            newAuthToken = tokenDto.accessToken,
            newRefreshToken = tokenDto.refreshToken,
            expiresIn = tokenDto.expiresIn
        ) ?: throw IllegalStateException("Unable to save token!")
    }

    override fun getSavedToken(username: String): KickToken? {
        return kickTokenRepository.getSavedToken(username)
    }
}