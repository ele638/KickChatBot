package ru.ele638.mychatbot.kickClient.models.events.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Sender(
    @SerialName("is_anonymous")
    val isAnonymous: Boolean,
    @SerialName("is_verified")
    val isVerified: Boolean,
    @SerialName("user_id")
    val userId: Int,
    @SerialName("username")
    val userName: String
)