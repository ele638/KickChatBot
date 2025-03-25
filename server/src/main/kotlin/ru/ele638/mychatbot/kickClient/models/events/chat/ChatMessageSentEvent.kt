package ru.ele638.mychatbot.kickClient.models.events.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.ele638.mychatbot.kickClient.models.events.EventPayload
import ru.ele638.mychatbot.kickClient.models.events.common.Broadcaster
import ru.ele638.mychatbot.kickClient.models.events.common.Sender

@Serializable
data class ChatMessageSentEvent(
    @SerialName("message_id")
    val messageId: String,
    @SerialName("broadcaster")
    val broadcaster: Broadcaster,
    @SerialName("sender")
    val sender: Sender,
    @SerialName("content")
    val content: String,
) : EventPayload