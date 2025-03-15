package ru.ele638.mychatbot.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MyChatBotTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme.copy()
    val typography = MaterialTheme.typography.copy()
    val shapes = MaterialTheme.shapes.copy()
    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}