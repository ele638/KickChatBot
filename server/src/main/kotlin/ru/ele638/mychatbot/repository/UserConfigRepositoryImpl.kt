package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert
import ru.ele638.mychatbot.database.tables.UserConfigs
import ru.ele638.mychatbot.database.tables.Users

interface UserConfigRepository {
    fun updateKickSecrets(username: String, clientId: String, clientSecret: String): Boolean
}

class UserConfigRepositoryImpl : UserConfigRepository {
    override fun updateKickSecrets(username: String, clientId: String, clientSecret: String): Boolean {
        return transaction {
            val existingUser = Users.select { Users.username eq username }.singleOrNull()
            if (existingUser == null) return@transaction false
            UserConfigs.upsert(
                UserConfigs.userId,
                onUpdate = mutableListOf(
                    UserConfigs.kickClientId to stringLiteral(clientId),
                    UserConfigs.kickClientSecret to stringLiteral(clientSecret),
                )
            ) {
                it[userId] = existingUser[id].value
                it[kickClientId] = clientId
                it[kickClientSecret] = clientSecret
            }
            true
        }
    }
}