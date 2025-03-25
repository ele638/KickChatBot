package ru.ele638.mychatbot.kickClient.models.events

import kotlinx.serialization.KSerializer
import ru.ele638.mychatbot.app.data.kick.network.EventType
import ru.ele638.mychatbot.kickClient.models.events.chat.ChatMessageSentEvent

enum class EventTypeSerializer(
    val eventType: EventType,
    val serializedName: String,
    val serializer: KSerializer<out EventPayload>,
    val version: Int
) {
    CHAT_MESSAGE_SENT(
        eventType = EventType.CHAT_MESSAGE_SENT,
        serializedName = "chat.message.sent",
        serializer = ChatMessageSentEvent.serializer(),
        version = 1
    );

    fun toEvent() = Event(
        name = serializedName,
        version = version
    )

    companion object {
        fun findByEventType(eventType: EventType): EventTypeSerializer {
            return requireNotNull(entries.find { it.eventType == eventType }) { "Unsupported EventType!" }
        }

        fun findBySerializedName(serializedName: String): EventTypeSerializer {
            return requireNotNull(entries.find { it.serializedName == serializedName }) { "Unsupported serialized event name!" }
        }
    }
}

interface EventPayload