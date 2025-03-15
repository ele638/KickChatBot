package ru.ele638.mychatbot.app.util

import kotlinx.coroutines.Dispatchers

class WasmDispatchersProvider : DispatchersProvider {
    override val Main = Dispatchers.Main
    override val Default = Dispatchers.Default
    override val Unconfined = Dispatchers.Unconfined
    override val IO = Dispatchers.Default
}