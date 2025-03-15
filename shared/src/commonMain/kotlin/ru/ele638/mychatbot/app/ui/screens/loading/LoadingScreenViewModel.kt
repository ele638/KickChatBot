package ru.ele638.mychatbot.app.ui.screens.loading

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.ele638.mychatbot.app.domain.session.SessionInteractor

class LoadingScreenViewModel(
    private val sessionInteractor: SessionInteractor
) : ViewModel() {
    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    fun checkSession() {
        viewModelScope.launch {
            val session = sessionInteractor.getSession()
            if (session == null) {
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