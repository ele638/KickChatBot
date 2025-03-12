package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}