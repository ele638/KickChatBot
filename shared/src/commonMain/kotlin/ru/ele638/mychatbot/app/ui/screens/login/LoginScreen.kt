package ru.ele638.mychatbot.app.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ru.ele638.mychatbot.app.ui.screens.setup.SETUP_ROUTE
import ru.ele638.mychatbot.app.ui.theme.MyChatBotTheme

const val LOGIN_ROUTE = "Login_screen"

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val viewModel: LoginScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState(null)

    LaunchedEffect(state) {
        if (state == LoginScreenViewModel.ScreenState.Success) {
            navController.navigate(SETUP_ROUTE)
        }
    }

    LoginScreenContent(
        state = state,
        modifier = modifier,
        loginAction = viewModel::login,
    )
}

@Composable
fun LoginScreenContent(
    state: LoginScreenViewModel.ScreenState?,
    modifier: Modifier = Modifier,
    loginAction: (server: String, login: String, password: String) -> Unit = { _, _, _ -> }
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var server by rememberSaveable {
            mutableStateOf("")
        }
        var login by rememberSaveable {
            mutableStateOf("")
        }
        var password by rememberSaveable {
            mutableStateOf("")
        }
        var passwordVisible by rememberSaveable { mutableStateOf(false) }

        TextField(
            value = server,
            onValueChange = { server = it },
            enabled = state != LoginScreenViewModel.ScreenState.Loading,
            isError = state is LoginScreenViewModel.ScreenState.Error,
            label = {
                Text(
                    text = "Server",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = login,
            onValueChange = { login = it },
            enabled = state != LoginScreenViewModel.ScreenState.Loading,
            isError = state is LoginScreenViewModel.ScreenState.Error,
            label = {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        )
        Spacer(Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            enabled = state != LoginScreenViewModel.ScreenState.Loading,
            isError = state is LoginScreenViewModel.ScreenState.Error,
            label = {
                Text(
                    text = "Password",
                    style = MaterialTheme.typography.labelSmall
                )
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                // Please provide localized description for accessibility services
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            }
        )
        Spacer(Modifier.height(8.dp))
        Button(
            enabled = state != LoginScreenViewModel.ScreenState.Loading && server.isNotEmpty() && login.isNotEmpty() && password.isNotEmpty(),
            onClick = { loginAction(server, login, password) }
        ) {
            when (state) {
                is LoginScreenViewModel.ScreenState.Error -> Text(
                    text = "Try again",
                    style = MaterialTheme.typography.bodyMedium
                )

                LoginScreenViewModel.ScreenState.Loading -> CircularProgressIndicator()
                else -> Text(
                    text = "Login",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    MyChatBotTheme {
        Surface {
            LoginScreenContent(state = null)
        }
    }
}