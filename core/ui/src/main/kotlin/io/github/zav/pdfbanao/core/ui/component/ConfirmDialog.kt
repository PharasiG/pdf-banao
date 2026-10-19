package io.github.zav.pdfbanao.core.ui.component

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.preview.PreviewLightDarkHindiBigFont
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme

/**
 * Asks "Are you sure?" before something important, like deleting a PDF.
 *
 * The confirm button is a filled button so it's easy to find; with [isDestructive] it is red,
 * for things that can't be undone. Tapping outside the dialog or pressing Back calls [onDismiss].
 * Long messages scroll, so nothing is cut off at big font sizes.
 */
@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dismissLabel: String = stringResource(R.string.cancel),
    isDestructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = if (isDestructive) {
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                } else {
                    ButtonDefaults.buttonColors()
                },
            ) {
                Text(confirmLabel)
            }
        },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissLabel)
            }
        },
        title = { Text(title) },
        text = {
            Text(
                text = message,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        },
    )
}

@PreviewLightDarkHindiBigFont
@Composable
private fun ConfirmDialogPreview() {
    PdfBanaoTheme {
        ConfirmDialog(
            title = stringResource(R.string.delete_pdf_title),
            message = stringResource(R.string.delete_pdf_message),
            confirmLabel = stringResource(R.string.delete),
            onConfirm = {},
            onDismiss = {},
            isDestructive = true,
        )
    }
}
