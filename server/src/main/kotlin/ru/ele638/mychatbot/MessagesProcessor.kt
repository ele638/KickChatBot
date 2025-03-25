package ru.ele638.mychatbot

import io.github.aakira.napier.Napier
import ru.ele638.mychatbot.kickClient.KickClient
import ru.ele638.mychatbot.kickClient.models.events.chat.ChatMessageSentEvent
import ru.ele638.mychatbot.repository.UserRepository

interface MessagesProcessor {
    suspend fun processMessage(event: ChatMessageSentEvent)
}

class MessagesProcessorImpl(
    private val kickClient: KickClient,
    private val userRepository: UserRepository
) : MessagesProcessor {
    override suspend fun processMessage(event: ChatMessageSentEvent) {
        Napier.v { "Incoming message: $event" }
        if (event.sender.userName.endsWith("appbot", true)) {
            return
        }
        val broadcasterId = event.broadcaster.userId
        val username = userRepository.requireUserByBroadcasterId(broadcasterId).username
        val message = "Hello, ${event.sender.userName}! Your message is ${event.content}"
        kickClient.postMessageIntoChat(username, message)
    }
}