package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.repository.KickEventSubscriptionsRepository
import ru.ele638.mychatbot.repository.KickEventSubscriptionsRepositoryImpl
import ru.ele638.mychatbot.repository.KickSessionRepository
import ru.ele638.mychatbot.repository.KickSessionRepositoryImpl
import ru.ele638.mychatbot.repository.KickTokenRepository
import ru.ele638.mychatbot.repository.KickTokenRepositoryImpl

val databaseModule = module {
    single<KickSessionRepository> { KickSessionRepositoryImpl(get(), get()) }
    single<KickTokenRepository> { KickTokenRepositoryImpl(get()) }
    single<KickEventSubscriptionsRepository> { KickEventSubscriptionsRepositoryImpl(get()) }
}