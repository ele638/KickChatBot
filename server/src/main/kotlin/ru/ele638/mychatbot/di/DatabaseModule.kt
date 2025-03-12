package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.database.DatabaseFactory
import ru.ele638.mychatbot.repository.UserRepository

val databaseModule = module {
    single { DatabaseFactory.init() }
    single { UserRepository(get()) }
}