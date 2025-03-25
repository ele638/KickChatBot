package ru.ele638.mychatbot.kickClient.models.events

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Event(
    @SerialName("name")
    val name: String,
    @SerialName("version")
    val version: Int
)