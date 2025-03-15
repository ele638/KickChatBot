package ru.ele638.mychatbot.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.ele638.mychatbot.ui.screens.loading.LoadingScreenViewModel
import ru.ele638.mychatbot.ui.screens.login.LoginScreenViewModel

val viewModelModule = module {
    viewModel { LoadingScreenViewModel(get()) }
    viewModel { LoginScreenViewModel(get()) }
}