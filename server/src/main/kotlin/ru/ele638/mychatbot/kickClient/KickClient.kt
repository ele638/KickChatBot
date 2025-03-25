package ru.ele638.mychatbot.kickClient

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.ele638.mychatbot.data.KickToken
import ru.ele638.mychatbot.kickClient.models.PostMessageRequest

interface KickClient {
    suspend fun postMessageIntoChat(username: String, content: String)
}

class KickClientImpl(
    private val kickTokenManager: KickTokenManager
) : KickClient {
    private val baseKickHost = "api.kick.com"
    private val baseKickAuthUrl = "https://$baseKickHost"

    private fun getHttpClient(username: String) = HttpClient {
        defaultRequest {
            url(baseKickAuthUrl)
            contentType(ContentType.Application.Json)
            header("Host", baseKickHost)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = loadToken(username)
                    if (token == null) {
                        Napier.w { "No token for KickClient!" }
                    }
                    token?.let {
                        BearerTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
                    }
                }
                refreshTokens {
                    refreshToken(username)
                    val token = loadToken(username)
                    if (token == null) {
                        Napier.w { "No token after refresh for KickClient!" }
                    }
                    token?.let {
                        BearerTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
                    }
                }
            }
        }
    }

    override suspend fun postMessageIntoChat(username: String, content: String) {
        val client = getHttpClient(username)
        val request = client.post("public/v1/chat") {
            setBody(
                PostMessageRequest(
                    content = content,
                    type = "bot"
                )
            )
        }
        if (request.status != HttpStatusCode.OK) {
            throw IllegalStateException("Error posting message: Status=${request.status}, body=${request.bodyAsText()}")
        }
    }

    private fun loadToken(username: String): KickToken? {
        return kickTokenManager.getSavedToken(username)
    }

    private suspend fun refreshToken(username: String) {
        kickTokenManager.refreshAndSaveToken(username)
    }
}