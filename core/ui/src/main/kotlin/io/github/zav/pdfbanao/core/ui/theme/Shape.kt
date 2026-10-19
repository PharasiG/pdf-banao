package io.github.zav.pdfbanao.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Corner roundness. A bit rounder than Material's defaults, for a softer, friendlier look.
internal val PdfBanaoShapes = Shapes(
    extraSmall = RoundedCornerShape(size = 6.dp),
    small = RoundedCornerShape(size = 10.dp),
    medium = RoundedCornerShape(size = 16.dp),
    large = RoundedCornerShape(size = 24.dp),
    extraLarge = RoundedCornerShape(size = 32.dp),
)
