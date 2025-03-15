package ru.ele638.mychatbot.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.ele638.mychatbot.app.domain.session.SessionInteractor


class LoginScreenViewModel(
    private val sessionInteractor: SessionInteractor
) : ViewModel() {
    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _state.emit(ScreenState.Loading)
            delay(1000)
            try {
                if (userName == "admin" && password == "123") {
                    Napier.i("Login VM passed login")
                    Napier.i("Saving session LoginVM")
                    sessionInteractor.saveSession(userName)
                    Napier.i("emit new status")
                    _state.emit(ScreenState.Success)
                } else {
                    throw IllegalStateException("YAY")
                }
            } catch (e: Exception) {
                _state.emit(ScreenState.Error(e.cause))
            }
        }
    }

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Error(val throwable: Throwable?) : ScreenState()
        data object Success : ScreenState()
    }
}