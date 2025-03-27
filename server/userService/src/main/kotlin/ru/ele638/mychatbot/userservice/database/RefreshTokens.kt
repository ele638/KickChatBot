package ru.ele638.mychatbot.userservice.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object RefreshTokens : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val refreshToken = text("refresh_token")
    val expiresAt = datetime("expires_at")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}