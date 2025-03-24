package ru.ele638.mychatbot

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.koin.compose.KoinApplication
import ru.ele638.mychatbot.app.ui.MyChatBotApp
import ru.ele638.mychatbot.di.kickModule
import ru.ele638.mychatbot.di.loginModule
import ru.ele638.mychatbot.di.sharedModule
import ru.ele638.mychatbot.di.viewModelModule

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        KoinApplication(application = {
            modules(sharedModule, viewModelModule, loginModule, kickModule)
        }) {
            MyChatBotApp()
        }
    }
}