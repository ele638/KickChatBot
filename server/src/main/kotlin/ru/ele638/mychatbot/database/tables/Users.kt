package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val username = varchar("username", 255).uniqueIndex()
    val password = varchar("password", 255)
}