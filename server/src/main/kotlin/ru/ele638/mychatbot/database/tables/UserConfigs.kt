package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import ru.ele638.mychatbot.database.tables.RefreshTokens.references

object UserConfigs : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val kickClientId = varchar("kick_client_id", 100).nullable()
    val kickClientSecret = varchar("kick_client_secret", 100).nullable()
    val kickPermissionCode = varchar("kick_permission_code", 100).nullable()
}