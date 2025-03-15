package ru.ele638.mychatbot.app.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.ele638.mychatbot.app.ui.screens.loading.LOADING_ROUTE
import ru.ele638.mychatbot.app.ui.screens.loading.LoadingScreen
import ru.ele638.mychatbot.app.ui.screens.login.LOGIN_ROUTE
import ru.ele638.mychatbot.app.ui.screens.login.LoginScreen
import ru.ele638.mychatbot.app.ui.screens.setup.SETUP_ROUTE
import ru.ele638.mychatbot.app.ui.screens.setup.SetupScreen
import ru.ele638.mychatbot.app.ui.theme.MyChatBotTheme

@Composable
fun MyChatBotApp() {
    Napier.base(DebugAntilog())
    MyChatBotTheme {
        MyChatBotContent()
    }
}

@Composable
fun MyChatBotContent(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { contentPadding ->
        val navController = rememberNavController()
        NavHost(
            modifier = Modifier.padding(contentPadding),
            navController = navController,
            startDestination = LOADING_ROUTE
        ) {
            composable(route = LOADING_ROUTE) {
                LoadingScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }
            composable(route = LOGIN_ROUTE) {
                LoginScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }
            composable(route = SETUP_ROUTE) {
                SetupScreen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }
        }
    }
}

@Composable
@Preview
fun MyChatBotPreview() {
    MyChatBotContent()
}
