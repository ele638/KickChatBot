package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ele638.mychatbot.data.KickEventSubscription
import ru.ele638.mychatbot.database.tables.KickEventsSubscriptions
import ru.ele638.mychatbot.kickClient.models.events.EventTypeSerializer

interface KickEventSubscriptionsRepository {
    fun saveSubscription(
        username: String,
        subId: String,
        event: EventTypeSerializer,
        subVersion: Int
    )

    fun getSubscriptions(username: String): List<KickEventSubscription>
    fun removeSubscription(username: String, subId: String)
    fun removeSubscriptions(username: String, subs: List<String>)
}

class KickEventSubscriptionsRepositoryImpl(
    private val userRepository: UserRepository
) : KickEventSubscriptionsRepository {
    override fun saveSubscription(
        username: String, subId: String, event: EventTypeSerializer, subVersion: Int
    ) {
        return transaction {
            val user = userRepository.requireUser(username)
            KickEventsSubscriptions.insert {
                it[userId] = user.userId
                it[subscriptionId] = subId
                it[version] = subVersion
                it[subscriptionName] = event.serializedName
            }
        }
    }

    override fun getSubscriptions(username: String): List<KickEventSubscription> {
        return transaction {
            val user = userRepository.requireUser(username)
            KickEventsSubscriptions.select {
                KickEventsSubscriptions.userId eq user.userId
            }.map {
                KickEventSubscription(
                    id = it[KickEventsSubscriptions.id].value,
                    subscriptionId = it[KickEventsSubscriptions.subscriptionId],
                    eventTypeSerializer = EventTypeSerializer.findBySerializedName(it[KickEventsSubscriptions.subscriptionName])
                )
            }
        }
    }

    override fun removeSubscription(username: String, subId: String) {
        return transaction {
            val user = userRepository.requireUser(username)
            KickEventsSubscriptions.deleteWhere {
                (userId eq user.userId) and (subscriptionId eq subId)
            }
        }
    }

    override fun removeSubscriptions(username: String, subs: List<String>) {
        return transaction {
            val user = userRepository.requireUser(username)
            KickEventsSubscriptions.deleteWhere {
                (userId eq user.userId) and (subscriptionId inList subs)
            }
        }
    }
}