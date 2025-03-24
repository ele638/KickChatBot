package ru.ele638.mychatbot.di

import org.koin.dsl.module
import ru.ele638.mychatbot.utils.CodeChallengeGenerator
import ru.ele638.mychatbot.utils.PasswordUtil

val utilModule = module {
    single { CodeChallengeGenerator() }
    single { PasswordUtil() }
}