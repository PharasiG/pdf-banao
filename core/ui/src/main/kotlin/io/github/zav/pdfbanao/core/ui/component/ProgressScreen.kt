package io.github.zav.pdfbanao.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme

/**
 * The full screen shown while a long job runs, like making a PDF: a [title], a progress bar,
 * "Page 7 of 30", and a big Cancel button.
 *
 * Shows [current] of [total] pages. While [total] is 0 (the job hasn't counted its pages yet),
 * the bar just moves and no page number is shown. The screen that uses this decides what the
 * Back button does, usually the same as [onCancel].
 */
@Composable
fun ProgressScreen(
    title: String,
    current: Int,
    total: Int,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        // Centred when it fits; scrolls when it doesn't (big font size, phone turned sideways).
        Box(
            modifier = Modifier.safeDrawingPadding(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                val barModifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                if (total > 0) {
                    val done = current.coerceIn(0, total)
                    LinearProgressIndicator(
                        progress = { done.toFloat() / total },
                        modifier = barModifier,
                    )
                    Text(
                        text = stringResource(R.string.progress_page_of, done, total),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                    )
                } else {
                    LinearProgressIndicator(modifier = barModifier)
                }
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp),
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@PreviewLightDarkHindiBigFont
@Composable
private fun ProgressScreenPreview() {
    PdfBanaoTheme {
        ProgressScreen(
            title = stringResource(R.string.making_pdf),
            current = 7,
            total = 30,
            onCancel = {},
        )
    }
}

@Preview(name = "Not counted yet", showBackground = true)
@Composable
private fun ProgressScreenStartingPreview() {
    PdfBanaoTheme {
        ProgressScreen(
            title = stringResource(R.string.making_pdf),
            current = 0,
            total = 0,
            onCancel = {},
        )
    }
}
