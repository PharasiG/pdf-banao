package io.github.zav.pdfbanao

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme
import io.github.zav.pdfbanao.navigation.PdfBanaoNavHost

/** The app's only Activity. Every screen is a Compose destination inside [PdfBanaoNavHost]. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            PdfBanaoTheme {
                PdfBanaoNavHost()
            }
        }
    }
}
