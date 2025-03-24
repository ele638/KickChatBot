package ru.ele638.mychatbot.app.data.network.dto

import kotlinx.serialization.Serializable

// Request & Response Data Classes
@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class RefreshRequest(val username: String, val refreshToken: String)

@Serializable
data class TokenVerifyRequest(val accessToken: String, val refreshToken: String)

@Serializable
data class AuthResponse(val accessToken: String, val refreshToken: String)

@Serializable
data class TokenResponse(val accessToken: String)

@Serializable
data class ErrorResponse(val message: String)

@Serializable
enum class TokenVerificationResult {
    TOKEN_VALID, NEED_REFRESH, TOKEN_INVALID
}