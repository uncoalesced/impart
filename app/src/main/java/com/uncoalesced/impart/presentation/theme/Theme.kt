// Engineered by uncoalesced
package com.uncoalesced.impart.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ImpartPanicRed,
    secondary = ImpartSecondary,
    background = ImpartBackground,
    surface = ImpartBackground,
    onPrimary = ImpartTextPrimary,
    onSecondary = ImpartTextPrimary,
    onBackground = ImpartTextPrimary,
    onSurface = ImpartTextPrimary,
    error = ImpartPanicDark
)

@Composable
fun ImpartTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

