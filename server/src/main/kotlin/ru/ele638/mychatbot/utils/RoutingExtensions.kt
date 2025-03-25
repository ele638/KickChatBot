package ru.ele638.mychatbot.utils

import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.RoutingContext

fun RoutingContext.getUsernameFromJWT(): String? {
    return call.principal<JWTPrincipal>()
        ?.payload
        ?.getClaim("username")
        ?.asString()
}