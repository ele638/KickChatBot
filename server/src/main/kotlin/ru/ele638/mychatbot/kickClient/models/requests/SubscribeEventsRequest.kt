package ru.ele638.mychatbot.kickClient.models.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.ele638.mychatbot.kickClient.models.events.Event

@Serializable
data class SubscribeEventsRequest(
    @SerialName("events")
    val events: List<Event>,
    @SerialName("method")
    val method: String
)