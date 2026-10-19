package io.github.zav.pdfbanao.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * The app's theme. Wrap every screen in this.
 *
 * Always uses the app's own colours, never the phone's wallpaper colours ("Material You"),
 * so the app looks the same on every phone. Light or dark follows the phone's setting.
 */
@Composable
fun PdfBanaoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = PdfBanaoTypography,
        shapes = PdfBanaoShapes,
        content = content,
    )
}
