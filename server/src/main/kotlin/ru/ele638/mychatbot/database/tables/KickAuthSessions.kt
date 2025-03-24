package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object KickAuthSessions : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val codeChallenge = varchar("code_challenge", 100)
    val codeVerifier = varchar("code_verifier", 100)
    val appCallbackUri = varchar("app_callback_uri", 255)
}