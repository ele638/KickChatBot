package ru.ele638.mychatbot.kickClient.models.events.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Broadcaster(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("username")
    val userName: String
)