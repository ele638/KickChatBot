package ru.ele638.mychatbot.common.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.transaction

private val DATABASE_URL = System.getenv("DATABASE_URL")
private val DATABASE_NAME = System.getenv("DATABASE_NAME")
private val DATABASE_PORT = System.getenv("DATABASE_PORT")
private val DATABASE_USER = System.getenv("DATABASE_USER")
private val DATABASE_PASSWORD = System.getenv("DATABASE_PASSWORD")

object DatabaseFactory {
    private val databaseConfig = HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://$DATABASE_URL:$DATABASE_PORT/$DATABASE_NAME"
        username = DATABASE_USER
        password = DATABASE_PASSWORD
        driverClassName = "org.postgresql.Driver"
        maximumPoolSize = 10
    }

    init {
        Database.connect(HikariDataSource(databaseConfig))
    }

    fun <T : Table> initDatabase(vararg tables: T) {
        transaction {
            SchemaUtils.create(*tables) // Ensure tables exist
        }
    }
}