package ru.ele638.mychatbot.repository

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.ele638.mychatbot.database.tables.Users

class UserRepository(
    private val db: Database
) {
    fun addUser(name: String): Int = transaction(db) {
        Users.insert {
            it[Users.name] = name
        } get Users.id
    }

    fun getUsers(): List<String> = transaction(db) {
        Users.selectAll().map { it[Users.name] }
    }
}