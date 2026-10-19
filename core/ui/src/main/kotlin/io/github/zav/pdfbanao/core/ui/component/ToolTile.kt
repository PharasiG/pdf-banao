package io.github.zav.pdfbanao.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme

/**
 * One tool in a grid of tools: an icon with its name underneath, like "Join PDFs".
 *
 * The whole tile is one button. TalkBack reads the [label]; the icon is only decoration.
 * The label wraps onto more lines instead of being cut off. To give every tile in a row the same
 * height, put the row in `Row(Modifier.height(IntrinsicSize.Min))` and give each tile
 * `Modifier.weight(1f).fillMaxHeight()`.
 */
@Composable
fun ToolTile(
    label: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .heightIn(min = 112.dp)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.tertiaryContainer, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@PreviewLightDarkHindiBigFont
@Composable
private fun ToolTilePreview() {
    PdfBanaoTheme {
        Surface {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ToolTile(
                    label = stringResource(R.string.tool_make_smaller),
                    icon = painterResource(R.drawable.ic_compress),
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
                ToolTile(
                    label = stringResource(R.string.tool_lock),
                    icon = painterResource(R.drawable.ic_lock),
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }
    }
}
