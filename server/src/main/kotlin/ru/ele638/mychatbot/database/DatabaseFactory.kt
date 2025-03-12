package ru.ele638.mychatbot.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.ele638.mychatbot.database.tables.Users

private val DB_URL = System.getenv("DB_URL")
private val DB_PORT = System.getenv("DB_PORT")
private val DB_NAME = System.getenv("DB_NAME")
private val DB_USER = System.getenv("DB_USER")
private val DB_PASSWORD = System.getenv("DB_PASSWORD")

object DatabaseFactory {
    fun init(): Database {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://$DB_URL:$DB_PORT/$DB_NAME"
            driverClassName = "org.postgresql.Driver"
            username = DB_USER
            password = DB_PASSWORD
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        return Database.connect(dataSource).apply {
            transaction {
                SchemaUtils.create(Users) // Ensure tables exist
            }
        }
    }
}