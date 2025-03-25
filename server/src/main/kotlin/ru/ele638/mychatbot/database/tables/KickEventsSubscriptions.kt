package ru.ele638.mychatbot.database.tables

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object KickEventsSubscriptions : IntIdTable() {
    val userId = integer("user_id").references(Users.id, onDelete = ReferenceOption.CASCADE)
    val subscriptionId = text("subscription_id")
    val version = integer("version")
    val subscriptionName = text("subscription_name")
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
}