package ru.ele638.mychatbot.app

import android.app.Application
import org.koin.core.context.startKoin
import ru.ele638.mychatbot.di.sharedModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(sharedModule)
        }
    }
}