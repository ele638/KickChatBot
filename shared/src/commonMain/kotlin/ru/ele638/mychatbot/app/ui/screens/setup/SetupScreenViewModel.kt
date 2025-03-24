package ru.ele638.mychatbot.app.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.publish
import kotlinx.coroutines.launch
import ru.ele638.mychatbot.app.domain.kick.KickConfigInteractor
import ru.ele638.mychatbot.app.domain.kick.KickToken

class SetupScreenViewModel(
    private val kickConfigInteractor: KickConfigInteractor
) : ViewModel() {
    private val _state = MutableStateFlow(ScreenState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val config = kickConfigInteractor.getSavedConfig()
            config?.let {
                _state.emit(
                    ScreenState(
                        clientId = it.clientId,
                        clientSecret = it.clientSecret
                    )
                )
            }

            val code = kickConfigInteractor.checkDeepLink()
            code?.let {
                _state.emit(
                    _state.value.copy(
                        callbackCode = it
                    )
                )
            }
        }
    }

    fun updateClientId(newClientId: String) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(clientId = newClientId))
        }
    }

    fun updateClientSecret(newClientSecret: String) {
        viewModelScope.launch {
            _state.emit(_state.value.copy(clientSecret = newClientSecret))
        }
    }

    fun requestCode() {
        viewModelScope.launch {
            _state.emit(_state.value.copy(isLoading = true))
            val state = _state.value
            kickConfigInteractor.requestOauthCode(
                clientId = state.clientId,
                clientSecret = state.clientSecret
            )
        }
    }

    fun generateToken() {
        viewModelScope.launch {
            val state = _state.value
            _state.emit(state.copy(isLoading = true))

            val newToken = kickConfigInteractor.requestToken(
                callbackCode = state.callbackCode,
                clientId = state.clientId,
                clientSecret = state.clientSecret
            )
            _state.emit(
                state.copy(token = newToken)
            )
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            val state = _state.value
            _state.emit(state.copy(isLoading = true))

            val newToken = kickConfigInteractor.refreshToken(
                refreshToken = state.token?.refreshToken ?: throw IllegalStateException("No refresh token!"),
                clientId = state.clientId,
                clientSecret = state.clientSecret
            )
            _state.emit(
                state.copy(token = newToken)
            )
        }
    }

    data class ScreenState(
        val clientId: String = "",
        val clientSecret: String = "",
        val callbackCode: String = "",
        val token: KickToken? = null,
        val isLoading: Boolean = false
    )
}