package ru.ele638.mychatbot.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

interface ClockProvider {
    fun now(): Instant
    fun parse(time: String): Instant
}

class ClockProviderImpl : ClockProvider {
    private val clock: Clock = Clock.System
    override fun now() = clock.now()
    override fun parse(time: String): Instant = Instant.parse(time)
}