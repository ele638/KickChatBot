package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object KickAuthSessions : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val codeChallenge = varchar("code_challenge", 100)
    val codeVerifier = varchar("code_verifier", 100)
    val appCallbackUri = varchar("app_callback_uri", 255)
    val clientId = varchar("client_id", 100)
    val clientSecret = varchar("client_secret", 100)
    val scopes = varchar("scopes", 255)
    val status = varchar("session_status", 20)
}