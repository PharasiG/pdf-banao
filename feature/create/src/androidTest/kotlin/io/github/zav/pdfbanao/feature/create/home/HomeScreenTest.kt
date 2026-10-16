package io.github.zav.pdfbanao.feature.create.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsAppNameAsTitle() {
        val appName = InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.app_name)

        composeTestRule.setContent {
            PdfBanaoTheme {
                HomeScreen(state = HomeUiState(), onEvent = {})
            }
        }

        composeTestRule.onNodeWithText(appName).assertIsDisplayed()
    }
}
