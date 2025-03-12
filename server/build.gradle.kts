plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

group = "ru.ele638.mychatbot"
version = "1.0.0"
application {
    mainClass.set("ru.ele638.mychatbot.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)
    testImplementation(libs.kotlin.test.junit)

    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.hikaricp) // Connection pooling
}