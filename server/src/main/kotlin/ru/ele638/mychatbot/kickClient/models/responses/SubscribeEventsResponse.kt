package ru.ele638.mychatbot.kickClient.models.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubscribeEventsResponse(
    @SerialName("message") val message: String,
    @SerialName("data") val data: List<SubscribeEventsResponseData>? = null
)

@Serializable
data class SubscribeEventsResponseData(
    @SerialName("name") val name: String,
    @SerialName("subscription_id") val subscriptionId: String? = null,
    @SerialName("error") val error: String? = null,
    @SerialName("version") val version: Int
)


