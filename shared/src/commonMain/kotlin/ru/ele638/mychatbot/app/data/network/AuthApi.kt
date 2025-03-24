package ru.ele638.mychatbot.app.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import ru.ele638.mychatbot.app.data.network.dto.AuthResponse
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.app.data.network.dto.LoginRequest
import ru.ele638.mychatbot.app.data.network.dto.RefreshRequest
import ru.ele638.mychatbot.app.data.network.dto.TokenVerificationResult
import ru.ele638.mychatbot.app.data.network.dto.TokenVerifyRequest
import ru.ele638.mychatbot.app.data.storage.PrefsProvider

interface AuthApi {
    suspend fun login(server: String, username: String, password: String): Result<Unit>
    suspend fun refreshToken(): Result<Unit>
    suspend fun verifyTokens(): Result<TokenVerificationResult>
}

class AuthApiImpl(
    private val prefsProvider: PrefsProvider
) : AuthApi {
    private val client = HttpClient {
        install(ContentNegotiation) { json() }
        headers {
            append(HttpHeaders.AccessControlAllowOrigin, "*")
        }
    }

    private var accessToken: String?
        get() = prefsProvider.get(ACCESS_TOKEN_KEY)
        set(newToken) = prefsProvider.put(ACCESS_TOKEN_KEY to requireNotNull(newToken))

    private var refreshToken: String?
        get() = prefsProvider.get(REFRESH_TOKEN_KEY)
        set(newToken) = prefsProvider.put(REFRESH_TOKEN_KEY to requireNotNull(newToken))

    private var serverUrl: String?
        get() = prefsProvider.get(SERVER_URL_KEY)
        set(newUrl) = prefsProvider.put(SERVER_URL_KEY to requireNotNull(newUrl))

    override suspend fun login(server: String, username: String, password: String): Result<Unit> {
        return try {
            val response = client.post("$server/login") {
                contentType(ContentType.Application.Json)
                setBody(LoginRequest(username, password))
            }

            if (response.status == HttpStatusCode.OK) {
                val authResponse: AuthResponse = response.body()
                accessToken = authResponse.accessToken
                refreshToken = authResponse.refreshToken
                serverUrl = server
                Result.success(Unit)
            } else {
                val error: ErrorResponse = response.body()
                Result.failure(Exception(error.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshToken(): Result<Unit> {
        return try {
            val response = client.post("$serverUrl/refreshToken") {
                contentType(ContentType.Application.Json)
                setBody(
                    RefreshRequest(
                        "admin",
                        refreshToken ?: return Result.failure(Exception("No refresh token"))
                    )
                )
            }

            if (response.status == HttpStatusCode.OK) {
                val tokenResponse: AuthResponse = response.body()
                accessToken = tokenResponse.accessToken
                Result.success(Unit)
            } else {
                val error: ErrorResponse = response.body()
                Result.failure(Exception(error.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun verifyTokens(): Result<TokenVerificationResult> {
        return try {
            val response = client.post("$serverUrl/verifyToken") {
                contentType(ContentType.Application.Json)
                setBody(
                    TokenVerifyRequest(
                        accessToken = accessToken ?: return Result.failure(Exception("No access token")),
                        refreshToken = refreshToken ?: return Result.failure(Exception("No refresh token"))
                    )
                )
            }

            if (response.status == HttpStatusCode.OK) {
                Result.success(response.body())
            } else {
                val error: ErrorResponse = response.body()
                Result.failure(Exception(error.message))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        const val ACCESS_TOKEN_KEY = "access_token"
        const val REFRESH_TOKEN_KEY = "refresh_token"
        const val SERVER_URL_KEY = "server_url"
    }
}