package ru.ele638.mychatbot

class JwmPlatform: Platform {
    override val name: String = "Jwm platform"
}

actual fun getPlatform(): Platform = JwmPlatform()
