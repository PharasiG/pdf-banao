package io.github.zav.pdfbanao.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Shows a preview four ways in Android Studio: light, dark, Hindi, and 200% font size.
 * Use it on preview functions instead of `@Preview`.
 */
@Preview(name = "Light", showBackground = true)
@Preview(
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL,
)
@Preview(name = "Hindi", showBackground = true, locale = "hi")
@Preview(name = "200% font", showBackground = true, fontScale = 2f)
annotation class PreviewLightDarkHindiBigFont
