package ru.ele638.mychatbot.util

import kotlinx.coroutines.Dispatchers

class AndroidDispatchersProvider : DispatchersProvider {
    override val Main = Dispatchers.Main
    override val Default = Dispatchers.Default
    override val Unconfined = Dispatchers.Unconfined
    override val IO = Dispatchers.IO
}

actual fun getDispatchersProvider(): DispatchersProvider = AndroidDispatchersProvider()