package ru.ele638.mychatbot.common.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.DecodedJWT
import java.util.Date

object JwtConfig {
    private val BE_JWT_SECRET = System.getenv("BE_JWT_SECRET")
    private val BE_JWT_REFRESH_SECRET = System.getenv("BE_JWT_REFRESH_SECRET")
    private const val ISSUER = "ktor_server"
    private const val AUDIENCE = "ktor_client"

    const val REFRESH_TOKEN_TTL_DAYS = 7L

    private const val ACCESS_EXPIRATION = 15 * 60 * 1000 // 15 minutes
    private const val REFRESH_EXPIRATION = REFRESH_TOKEN_TTL_DAYS * 24 * 60 * 60 * 1000 // 7 days

    private val algorithm = Algorithm.HMAC256(BE_JWT_SECRET)
    private val refreshAlgorithm = Algorithm.HMAC256(BE_JWT_REFRESH_SECRET)

    fun getJwtVerifier(): JWTVerifier = JWT
        .require(algorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()

    fun generateAccessToken(username: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("username", username)
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
            .sign(algorithm)
    }

    fun generateRefreshToken(username: String): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withSubject(username)
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
            .sign(refreshAlgorithm)
    }

    fun verifyToken(token: String): DecodedJWT = JWT
        .require(refreshAlgorithm)
        .withAudience(AUDIENCE)
        .withIssuer(ISSUER)
        .build()
        .verify(token)
}