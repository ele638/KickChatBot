package ru.ele638.mychatbot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform