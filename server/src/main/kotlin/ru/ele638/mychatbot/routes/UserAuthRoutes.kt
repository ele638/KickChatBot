package ru.ele638.mychatbot.routes

import JwtConfig
import com.auth0.jwt.exceptions.JWTVerificationException
import io.github.aakira.napier.Napier
import io.ktor.http.HttpStatusCode
import io.ktor.server.logging.toLogString
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.koin.ktor.ext.inject
import ru.ele638.mychatbot.app.data.network.dto.AuthResponse
import ru.ele638.mychatbot.app.data.network.dto.ErrorResponse
import ru.ele638.mychatbot.app.data.network.dto.LoginRequest
import ru.ele638.mychatbot.app.data.network.dto.RefreshRequest
import ru.ele638.mychatbot.app.data.network.dto.TokenResponse
import ru.ele638.mychatbot.app.data.network.dto.TokenVerificationResult
import ru.ele638.mychatbot.app.data.network.dto.TokenVerifyRequest
import ru.ele638.mychatbot.data.User
import ru.ele638.mychatbot.repository.UserRepository
import java.util.Date

fun Routing.authRoutes() {


}




