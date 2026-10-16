package io.github.zav.pdfbanao.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// Default Material 3 colours for now. Task 0.3 gives the app its own colours and type.
private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

/** The app's theme. Wrap every screen in this. */
@Composable
fun PdfBanaoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
