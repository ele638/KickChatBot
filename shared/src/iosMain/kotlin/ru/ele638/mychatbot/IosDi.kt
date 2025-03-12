package ru.ele638.mychatbot

import org.koin.core.context.startKoin
import ru.ele638.mychatbot.di.sharedModule

fun initKoin() {
    startKoin {
        modules(sharedModule)
    }
}