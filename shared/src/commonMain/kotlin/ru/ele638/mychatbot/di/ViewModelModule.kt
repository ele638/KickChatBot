package ru.ele638.mychatbot.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import ru.ele638.mychatbot.app.ui.screens.loading.LoadingScreenViewModel
import ru.ele638.mychatbot.app.ui.screens.login.LoginScreenViewModel
import ru.ele638.mychatbot.app.ui.screens.setup.SetupScreenViewModel

val viewModelModule = module {
    viewModel { LoadingScreenViewModel(get()) }
    viewModel { LoginScreenViewModel(get()) }
    viewModel { SetupScreenViewModel(get()) }
}