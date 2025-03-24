package ru.ele638.mychatbot.app.ui.screens.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinNavViewModel
import org.koin.compose.viewmodel.koinViewModel
import ru.ele638.mychatbot.app.domain.kick.KickToken
import ru.ele638.mychatbot.app.ui.theme.MyChatBotTheme

const val SETUP_ROUTE = "Setup_screen"

@Composable
fun SetupScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    val viewModel: SetupScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    SetupScreenContent(
        modifier = modifier,
        state = state,
        codeRequest = viewModel::requestCode,
        requestToken = viewModel::generateToken,
        refreshToken = viewModel::refreshToken,
        updateClientId = viewModel::updateClientId,
        updateClientSecret = viewModel::updateClientSecret
    )
}

@Composable
fun SetupScreenContent(
    modifier: Modifier = Modifier,
    state: SetupScreenViewModel.ScreenState = SetupScreenViewModel.ScreenState(),
    codeRequest: () -> Unit = { },
    requestToken: () -> Unit = { },
    refreshToken: () -> Unit = { },
    updateClientId: (clientId: String) -> Unit = { },
    updateClientSecret: (clientSecret: String) -> Unit = { }
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .wrapContentWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Setup your account",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(32.dp))
                TextField(
                    value = state.clientId,
                    onValueChange = updateClientId,
                    label = {
                        Text(
                            text = "Client ID",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = state.clientSecret,
                    onValueChange = updateClientSecret,
                    label = {
                        Text(
                            text = "Client secret",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = codeRequest,
//                    enabled = !state.isLoading && state.callbackCode.isEmpty()
                ) {
                    when {
                        state.isLoading -> CircularProgressIndicator()
                        else -> Text(
                            text = "Request code"
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                if (state.callbackCode.isNotEmpty()) {
                    Text(
                        text = "Callback code:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = state.callbackCode,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = { if (state.token == null) requestToken() else refreshToken() },
//                        enabled = !state.isLoading && state.callbackCode.isNotEmpty()
                    ) {
                        when {
                            state.isLoading -> CircularProgressIndicator()
                            else -> Text(
                                text = "Request token"
                            )
                        }
                    }
                }

                if (state.token != null) {
                    Text(
                        text = "Token:",
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = state.token.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun SetupScreenPreview() {
    MyChatBotTheme {
        Surface {
            SetupScreenContent(
                state = SetupScreenViewModel.ScreenState(
                    callbackCode = "12345",
                    token = KickToken(
                        token = "12345",
                        refreshToken = "0000",
                        expiresIn = "trtr"
                    )
                )
            )
        }
    }
}