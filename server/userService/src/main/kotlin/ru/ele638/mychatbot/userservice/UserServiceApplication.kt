package ru.ele638.mychatbot.userservice

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import ru.ele638.mychatbot.common.database.DatabaseFactory
import ru.ele638.mychatbot.common.modules.initModule
import ru.ele638.mychatbot.userservice.database.RefreshTokens
import ru.ele638.mychatbot.userservice.database.Users
import ru.ele638.mychatbot.userservice.di.userServiceModule
import ru.ele638.mychatbot.userservice.modules.authModule

private val BE_HOST = System.getenv("BE_HOST")
private val BE_PORT = System.getenv("BE_PORT").toInt()

fun main() {
    embeddedServer(
        Netty,
        port = BE_PORT,
        host = BE_HOST,
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    install(Koin) {
        slf4jLogger()
        modules(userServiceModule)
    }
    initModule(this)
    authModule(this)

    DatabaseFactory.initDatabase(
        Users, RefreshTokens
    )
}