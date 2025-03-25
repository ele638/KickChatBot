package ru.ele638.mychatbot.kickClient.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsersResponse(
    @SerialName("data")
    val data: List<UsersResponseData>? = null,
    @SerialName("message")
    val message: String? = null
)

@Serializable
data class UsersResponseData(
    @SerialName("name")
    val name: String,
    @SerialName("user_id")
    val userId: Int
)
