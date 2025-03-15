package ru.ele638.mychatbot.app.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

class IOSDispatchersProvider : DispatchersProvider {
    override val Main = Dispatchers.Main
    override val Default = Dispatchers.Default
    override val Unconfined = Dispatchers.Unconfined
    override val IO = Dispatchers.IO
}