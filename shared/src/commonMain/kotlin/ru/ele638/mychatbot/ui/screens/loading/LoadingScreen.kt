package ru.ele638.mychatbot.ui.screens.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import ru.ele638.mychatbot.ui.screens.login.LOGIN_ROUTE
import ru.ele638.mychatbot.ui.screens.setup.SETUP_ROUTE
import ru.ele638.mychatbot.ui.theme.MyChatBotTheme

const val LOADING_ROUTE = "Loading_screen"

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    val viewModel: LoadingScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState(null)
    LaunchedEffect(Unit) {
        viewModel.checkSession()
    }
    LaunchedEffect(state) {
        when (state) {
            LoadingScreenViewModel.ScreenState.LoginRequired -> navController.navigate(
                LOGIN_ROUTE,
                navOptions { launchSingleTop = true })

            LoadingScreenViewModel.ScreenState.SetupRequired -> navController.navigate(
                SETUP_ROUTE,
                navOptions { launchSingleTop = true })

            LoadingScreenViewModel.ScreenState.SkipLogin -> TODO()
            null -> return@LaunchedEffect
        }
    }
    LoadingScreenContent(modifier = modifier)

}

@Composable
fun LoadingScreenContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
private fun LoadingScreenPreview() {
    MyChatBotTheme {
        Surface {
            LoadingScreenContent()
        }
    }
}

