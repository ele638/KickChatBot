package ru.ele638.mychatbot.data

import java.time.LocalDateTime

data class KickToken(
    val tokenId: Int,
    val accessToken: String,
    val refreshToken: String,
    val expires: LocalDateTime
)
