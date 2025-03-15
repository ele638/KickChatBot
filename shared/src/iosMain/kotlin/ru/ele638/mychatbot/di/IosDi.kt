package ru.ele638.mychatbot.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}