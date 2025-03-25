package ru.ele638.mychatbot.kickClient

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import ru.ele638.mychatbot.data.KickToken
import ru.ele638.mychatbot.kickClient.models.events.EventTypeSerializer
import ru.ele638.mychatbot.kickClient.models.requests.PostMessageRequest
import ru.ele638.mychatbot.kickClient.models.requests.SubscribeEventsRequest
import ru.ele638.mychatbot.kickClient.models.responses.SubscribeEventsResponse
import ru.ele638.mychatbot.kickClient.models.responses.SubscribeEventsResponseData
import ru.ele638.mychatbot.kickClient.models.responses.UsersResponse
import ru.ele638.mychatbot.repository.KickEventSubscriptionsRepository

interface KickClient {
    suspend fun postMessageIntoChat(username: String, content: String)
    suspend fun subscribeToEvents(username: String, events: List<EventTypeSerializer>)
    suspend fun unsubscribeFromEvents(username: String, events: List<EventTypeSerializer>)
    suspend fun getBroadcasterId(userName: String): Int
}

class KickClientImpl(
    private val kickTokenManager: KickTokenManager,
    private val kickEventSubscriptionsRepository: KickEventSubscriptionsRepository
) : KickClient {
    private val baseKickHost = "api.kick.com"
    private val baseKickAuthUrl = "https://$baseKickHost"

    private val clients = hashMapOf<String, HttpClient>()

    private fun getHttpClient(username: String): HttpClient {
        val existingClient = clients[username]
        if (existingClient != null) {
            return existingClient
        }
        val newClient = initHttpClient(username)
        clients[username] = newClient
        return newClient
    }

    private fun initHttpClient(username: String) = HttpClient {
        defaultRequest {
            url(baseKickAuthUrl)
            contentType(ContentType.Application.Json)
            header("Host", baseKickHost)
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(Auth) {
            bearer {
                loadTokens {
                    val token = loadToken(username)
                    if (token == null) {
                        Napier.w { "No token for KickClient!" }
                    }
                    token?.let {
                        BearerTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
                    }
                }
                refreshTokens {
                    refreshToken(username)
                    val token = loadToken(username)
                    if (token == null) {
                        Napier.w { "No token after refresh for KickClient!" }
                    }
                    token?.let {
                        BearerTokens(accessToken = it.accessToken, refreshToken = it.refreshToken)
                    }
                }
            }
        }
    }

    override suspend fun postMessageIntoChat(username: String, content: String) {
        val client = getHttpClient(username)
        val request = client.post("public/v1/chat") {
            setBody(
                PostMessageRequest(
                    content = content,
                    type = "bot"
                )
            )
        }
        if (request.status != HttpStatusCode.OK) {
            throw IllegalStateException("Error posting message: Status=${request.status}, body=${request.bodyAsText()}")
        }
    }

    override suspend fun subscribeToEvents(username: String, events: List<EventTypeSerializer>) {
        val client = getHttpClient(username)
        val request = client.post("public/v1/events/subscriptions") {
            setBody(
                SubscribeEventsRequest(
                    events = events.map(EventTypeSerializer::toEvent),
                    method = "webhook"
                )
            )
        }
        val response: SubscribeEventsResponse = request.body()
        Napier.v { "Response body is: $response and data is ${response.data}" }
        if (request.status == HttpStatusCode.OK && response.data != null) {
            response.data
                .filter { it.subscriptionId != null }
                .forEach { data: SubscribeEventsResponseData ->
                    kickEventSubscriptionsRepository.saveSubscription(
                        username = username,
                        subId = requireNotNull(data.subscriptionId),
                        event = EventTypeSerializer.findBySerializedName(data.name),
                        subVersion = data.version
                    )
                }

            val errors = response.data
                .filter { it.subscriptionId == null && it.error != null }
                .map { it.error }
                .joinToString()
            if (errors.isNotBlank()) {
                throw IllegalStateException("Subscription errors: $errors")
            }
        } else {
            throw IllegalStateException("Subscribe to event returned error: code=${request.status}, body=${request.bodyAsText()}")
        }
    }

    override suspend fun unsubscribeFromEvents(
        username: String,
        events: List<EventTypeSerializer>
    ) {
        Napier.v { "Requested unsubscriptions: $events" }
        val savedSubscriptions = kickEventSubscriptionsRepository.getSubscriptions(username)
        Napier.v { "Saved subscriptions: $savedSubscriptions" }
        val filteredToUnsubscribeIds = savedSubscriptions.filter { subscription ->
            subscription.eventTypeSerializer.serializedName in events.map { it.serializedName }
        }.map { it.subscriptionId }

        Napier.v { "Filtered subscriptions: $filteredToUnsubscribeIds" }

        if (filteredToUnsubscribeIds.isEmpty()) {
            return
        }
        val client = getHttpClient(username)
        val request = client.delete("public/v1/events/subscriptions") {
            filteredToUnsubscribeIds.forEach { id ->
                parameter("id", id)
            }
        }
        if (request.status == HttpStatusCode.NoContent) {
            kickEventSubscriptionsRepository.removeSubscriptions(username, filteredToUnsubscribeIds)
        } else {
            throw IllegalStateException("Unsubscribe failed with error: code=${request.status}, body=${request.bodyAsText()}")
        }
    }

    override suspend fun getBroadcasterId(userName: String): Int {
        val client = getHttpClient(userName)
        val request = client.get("public/v1/users")
        val body: UsersResponse = request.body()
        if (request.status == HttpStatusCode.OK && body.data != null) {
            return body.data.first().userId
        } else {
            throw IllegalStateException("Broadcaster id can't be loaded. Error: code=${request.status}, body=${request.bodyAsText()}")
        }
    }

    private fun loadToken(username: String): KickToken? {
        return kickTokenManager.getSavedToken(username)
    }

    private suspend fun refreshToken(username: String) {
        kickTokenManager.refreshAndSaveToken(username)
    }
}