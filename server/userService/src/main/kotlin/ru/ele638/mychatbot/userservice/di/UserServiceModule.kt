package ru.ele638.mychatbot.userservice.di

import org.koin.dsl.module
import ru.ele638.mychatbot.userservice.repositories.UserRepository
import ru.ele638.mychatbot.userservice.repositories.UserRepositoryImpl
import ru.ele638.mychatbot.userservice.utils.PasswordUtil

val userServiceModule = module {
    single { PasswordUtil() }
    single<UserRepository> { UserRepositoryImpl(get()) }
}