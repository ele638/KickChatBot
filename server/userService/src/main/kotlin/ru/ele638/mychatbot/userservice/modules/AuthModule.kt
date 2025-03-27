package ru.ele638.mychatbot.userservice.modules

import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.logging.toLogString
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.AuthResponse
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.app.data.network.dto.LoginRequest
import ru.ele638.mychatbot.app.data.network.dto.RefreshRequest
import ru.ele638.mychatbot.app.data.network.dto.TokenVerificationResult
import ru.ele638.mychatbot.app.data.network.dto.TokenVerifyRequest
import ru.ele638.mychatbot.common.security.JwtConfig
import ru.ele638.mychatbot.userservice.repositories.UserRepository
import java.util.Date

fun authModule(application: Application) = with(application) {
    val userRepository: UserRepository by inject()
    routing {
        post("/user/login") {
            val request = call.receive<LoginRequest>()
            if (!userRepository.isUserPasswordCorrect(request.username, request.password)) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid credentials"))
                return@post
            }
            val accessToken = JwtConfig.generateAccessToken(request.username)
            val refreshToken = JwtConfig.generateRefreshToken(request.username)

            if(!userRepository.updateUserRefreshToken(request.username, refreshToken)){
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Unable to save refresh token"))
                return@post
            }

            call.respond(HttpStatusCode.OK, AuthResponse(accessToken, refreshToken))
        }

        post("/user/register") {
            Napier.v("DEBUG: ${call.request.toLogString()}")
            val request = call.receive<LoginRequest>()

            val userCreationResult = userRepository.createUser(request.username, request.password)
            if (userCreationResult.isSuccess) {
                call.respond(HttpStatusCode.OK)
            } else {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Unable to create user"))
            }
        }

        post("/user/refreshToken") {
            val request = call.receive<RefreshRequest>()
            val storedRefreshToken = userRepository.verifyUserRefreshToken(request.username, request.refreshToken)
            if (!storedRefreshToken) {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid refresh token"))
                return@post
            }

            val accessToken = JwtConfig.generateAccessToken(request.username)
            val refreshToken = JwtConfig.generateRefreshToken(request.username)

            if (!userRepository.updateUserRefreshToken(request.username, refreshToken)) {
                call.respond(HttpStatusCode.InternalServerError, ErrorResponse("Unable to save refresh token"))
                return@post
            }

            call.respond(HttpStatusCode.OK, AuthResponse(accessToken, refreshToken))
        }

        post("/user/verifyToken") {
            val request = call.receive<TokenVerifyRequest>()

            val isAccessTokenValid = try {
                val decodedToken = JwtConfig.verifyToken(request.accessToken)
                decodedToken.expiresAt > Date(System.currentTimeMillis())
            } catch (e: JWTVerificationException) {
                false
            }
            val isRefreshTokenValid = userRepository.verifyRefreshTokenExists(request.refreshToken)

            val response = when {
                isAccessTokenValid -> TokenVerificationResult.TOKEN_VALID
                isRefreshTokenValid -> TokenVerificationResult.NEED_REFRESH
                else -> TokenVerificationResult.TOKEN_INVALID
            }
            call.respond(HttpStatusCode.OK, response)
        }
    }
}