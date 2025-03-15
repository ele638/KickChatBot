package ru.ele638.mychatbot.app.ui.screens.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jetbrains.compose.ui.tooling.preview.Preview
import ru.ele638.mychatbot.app.ui.theme.MyChatBotTheme

const val SETUP_ROUTE = "Setup_screen"

@Composable
fun SetupScreen(modifier: Modifier = Modifier, navController: NavHostController) {
    SetupScreenContent(modifier = modifier)
}

@Composable
fun SetupScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

    }
}


@Preview
@Composable
private fun SetupScreenPreview() {
    MyChatBotTheme {
        Surface {
            SetupScreenContent()
        }
    }
}