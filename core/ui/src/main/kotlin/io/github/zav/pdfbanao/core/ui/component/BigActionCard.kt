package io.github.zav.pdfbanao.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme

/**
 * A big, easy-to-tap card for a main job, like "Photos to PDF" on Home.
 *
 * The whole card is one button. TalkBack reads the [title] and [description]; the icon is only
 * decoration, so it has no label of its own. The card grows taller when the text wraps
 * (big font sizes, long Hindi words).
 *
 * @param highlighted true for the most important card on a screen. It gets the strongest colour.
 */
@Composable
fun BigActionCard(
    title: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    description: String? = null,
    highlighted: Boolean = false,
) {
    val colors = MaterialTheme.colorScheme
    val iconBackground = if (highlighted) colors.onPrimary.copy(alpha = 0.16f) else colors.surface
    val iconTint = if (highlighted) colors.onPrimary else colors.primary
    Card(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        shape = MaterialTheme.shapes.large,
        colors = if (highlighted) {
            CardDefaults.cardColors(
                containerColor = colors.primary,
                contentColor = colors.onPrimary,
            )
        } else {
            CardDefaults.cardColors(
                containerColor = colors.secondaryContainer,
                contentColor = colors.onSecondaryContainer,
            )
        },
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = 88.dp)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBackground, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = iconTint,
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@PreviewLightDarkHindiBigFont
@Composable
private fun BigActionCardPreview() {
    PdfBanaoTheme {
        Surface {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                BigActionCard(
                    title = stringResource(R.string.home_photos_to_pdf),
                    description = stringResource(R.string.home_photos_to_pdf_hint),
                    icon = painterResource(R.drawable.ic_photo_library),
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    highlighted = true,
                )
                BigActionCard(
                    title = stringResource(R.string.home_scan),
                    description = stringResource(R.string.home_scan_hint),
                    icon = painterResource(R.drawable.ic_photo_camera),
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
