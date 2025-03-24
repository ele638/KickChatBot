package ru.ele638.mychatbot.app.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import ru.ele638.mychatbot.app.data.network.AuthApi
import ru.ele638.mychatbot.app.domain.session.AuthInteractor


class LoginScreenViewModel(
    private val authApi: AuthApi
) : ViewModel() {
    private val _state = MutableSharedFlow<ScreenState>()
    val state = _state.asSharedFlow()

    fun login(server: String, userName: String, password: String) {
        viewModelScope.launch {
            _state.emit(ScreenState.Loading)
            val result = authApi.login(
                server = server,
                username = userName,
                password = password
            )
            if (result.isSuccess) {
                _state.emit(ScreenState.Success)
            } else {
                _state.emit(ScreenState.Error(result.exceptionOrNull()))
            }
        }
    }

    sealed class ScreenState {
        data object Loading : ScreenState()
        data class Error(val throwable: Throwable?) : ScreenState()
        data object Success : ScreenState()
    }
}