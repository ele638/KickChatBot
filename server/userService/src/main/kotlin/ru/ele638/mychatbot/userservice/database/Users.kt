package ru.ele638.mychatbot.userservice.database

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
    val kickClientId = varchar("kick_client_id", 100).nullable()
    val kickClientSecret = varchar("client_secret", 255).nullable()
    val kickPermissionCode = varchar("kick_permission_code", 255).nullable()
    val kickBroadcasterId = integer("kick_broadcaster_id").nullable()
}