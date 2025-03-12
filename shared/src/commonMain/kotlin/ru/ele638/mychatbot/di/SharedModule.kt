package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.Platform
import ru.ele638.mychatbot.getPlatform

val sharedModule = module {
    single<Platform> { getPlatform() }
}