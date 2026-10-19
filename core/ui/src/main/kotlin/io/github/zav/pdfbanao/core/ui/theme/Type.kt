package io.github.zav.pdfbanao.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Text styles. We use the phone's own font: nothing to download, and Hindi shows in the phone's
// built-in Hindi font. Compared with Material's defaults:
// - Most text is about 2 sp bigger, because many users are older.
// - Lines are further apart. Hindi letters have marks above and below the line (ि, ी, ु, ृ),
//   and with tight lines they touch the line above or get cut off.
// - No extra space between letters: it breaks the line on top of Hindi words (शिरोरेखा).
// All sizes are in sp, so they grow with the phone's font size setting (up to 200%).

private val Default = Typography()

// Keep the full line height, with the text in the middle, so tall Hindi marks are never trimmed.
private val RoomForHindi = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None,
)

private fun TextStyle.sized(
    fontSize: TextUnit,
    lineHeight: TextUnit,
) = copy(
    fontSize = fontSize,
    lineHeight = lineHeight,
    letterSpacing = 0.sp,
    lineHeightStyle = RoomForHindi,
)

internal val PdfBanaoTypography = Typography(
    displayLarge = Default.displayLarge.sized(fontSize = 57.sp, lineHeight = 72.sp),
    displayMedium = Default.displayMedium.sized(fontSize = 45.sp, lineHeight = 60.sp),
    displaySmall = Default.displaySmall.sized(fontSize = 36.sp, lineHeight = 48.sp),
    headlineLarge = Default.headlineLarge.sized(fontSize = 32.sp, lineHeight = 44.sp),
    headlineMedium = Default.headlineMedium.sized(fontSize = 28.sp, lineHeight = 40.sp),
    headlineSmall = Default.headlineSmall.sized(fontSize = 24.sp, lineHeight = 34.sp),
    titleLarge = Default.titleLarge.sized(fontSize = 22.sp, lineHeight = 32.sp),
    titleMedium = Default.titleMedium.sized(fontSize = 18.sp, lineHeight = 26.sp),
    titleSmall = Default.titleSmall.sized(fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = Default.bodyLarge.sized(fontSize = 18.sp, lineHeight = 28.sp),
    bodyMedium = Default.bodyMedium.sized(fontSize = 16.sp, lineHeight = 24.sp),
    bodySmall = Default.bodySmall.sized(fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = Default.labelLarge.sized(fontSize = 16.sp, lineHeight = 24.sp),
    labelMedium = Default.labelMedium.sized(fontSize = 14.sp, lineHeight = 20.sp),
    labelSmall = Default.labelSmall.sized(fontSize = 12.sp, lineHeight = 18.sp),
)
