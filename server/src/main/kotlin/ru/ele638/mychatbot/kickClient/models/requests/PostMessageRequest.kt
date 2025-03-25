package ru.ele638.mychatbot.kickClient.models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostMessageRequest(
    @SerialName("content")
    val content: String,
    @SerialName("type")
    val type: String
)
