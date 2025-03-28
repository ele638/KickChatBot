plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.shadow)
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
}

group = "ru.ele638.mychatbot"
version = "1.0.0"

dependencies {
    api(projects.shared)

    // Client
    api(libs.ktor.client.auth)
    api(libs.ktor.client.cio)
    api(libs.ktor.client.content.negotiation)
    api(libs.ktor.client.logging)

    // Server
    api(libs.ktor.server.core)
    api(libs.ktor.server.netty)
    api(libs.ktor.server.defaultHeaders)
    api(libs.ktor.server.auth)
    api(libs.ktor.server.auth.jwt)
    api(libs.ktor.server.sessions)
    api(libs.ktor.server.cors)
    api(libs.ktor.server.content.negotiation)

    // Serialization
    api(libs.ktor.serialization.kotlinx.json)

    // DI (Koin)
    api(libs.koin.ktor)
    api(libs.koin.logger.slf4j)

    // Crypto
    api(libs.jbcrypt)
    api(kotlincrypto.hash.sha2)

    // Logging
    api(libs.logback)
    api(libs.napier)

    // Testing
    testApi(libs.kotlin.test.junit)

    // DB
    api(libs.exposed.core)
    api(libs.exposed.dao)
    api(libs.exposed.java.time)
    api(libs.exposed.jdbc)
    api(libs.postgresql)
    api(libs.hikaricp)

    // Protobuf
    api(libs.grpc.kotlin.stub)
    api(libs.grpc.netty.shaded)
    api(libs.grpc.protobuf)
    api(libs.protobuf.kotlin)

    // Metrics
    api(libs.ktor.server.metrics.micrometer)
    api(libs.micrometer.registry.prometheus)
}