package ru.ele638.mychatbot.app.ui.screens.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.ele638.mychatbot.app.domain.session.AuthInteractor

class LoadingScreenViewModel(
    private val authInteractor: AuthInteractor
) : ViewModel() {
    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    fun checkSession() {
        viewModelScope.launch {
            val isLoginRequired = authInteractor.isLoginRequired()
            if (isLoginRequired) {
                _state.emit(ScreenState.LoginRequired)
            } else {
                _state.emit(ScreenState.SetupRequired)
            }
        }
    }

    sealed class ScreenState {
        data object SkipLogin : ScreenState()
        data object LoginRequired : ScreenState()
        data object SetupRequired : ScreenState()
    }
}