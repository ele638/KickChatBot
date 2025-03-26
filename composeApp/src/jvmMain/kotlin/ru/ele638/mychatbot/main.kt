package ru.ele638.mychatbot

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import ru.ele638.mychatbot.app.ui.MyChatBotApp
import ru.ele638.mychatbot.di.kickModule
import ru.ele638.mychatbot.di.loginModule
import ru.ele638.mychatbot.di.sharedModule
import ru.ele638.mychatbot.di.viewModelModule

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "KotlinProject",
    ) {
        startKoin {
            modules(loginModule, sharedModule, viewModelModule, kickModule)
        }
        MyChatBotApp()
    }

}