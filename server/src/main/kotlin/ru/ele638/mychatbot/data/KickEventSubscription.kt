package ru.ele638.mychatbot.data

import ru.ele638.mychatbot.kickClient.models.events.EventTypeSerializer

data class KickEventSubscription(
    val id: Int,
    val eventTypeSerializer: EventTypeSerializer,
    val subscriptionId: String
)
