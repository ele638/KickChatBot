package ru.ele638.mychatbot.app.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class KickBotPostMessageRequest(
    val content: String
)