package ru.ele638.mychatbot.app.data.network.dto

import kotlinx.serialization.Serializable
import ru.ele638.mychatbot.app.data.kick.network.EventType

@Serializable
data class KickSubscribeEventsDTO(
    val events: List<EventType>
)