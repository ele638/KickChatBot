package ru.ele638.mychatbot.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.MainCoroutineDispatcher

interface DispatchersProvider {
    val Main: MainCoroutineDispatcher
    val Default: CoroutineDispatcher
    val Unconfined: CoroutineDispatcher
    val IO: CoroutineDispatcher
}

expect fun getDispatchersProvider(): DispatchersProvider

