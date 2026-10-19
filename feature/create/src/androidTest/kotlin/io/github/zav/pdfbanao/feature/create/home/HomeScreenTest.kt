package io.github.zav.pdfbanao.feature.create.home

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val events = mutableListOf<HomeEvent>()

    private fun text(@StringRes id: Int): String =
        InstrumentationRegistry.getInstrumentation().targetContext.getString(id)

    private fun showHome(state: HomeUiState = HomeUiState()) {
        composeTestRule.setContent {
            PdfBanaoTheme {
                HomeScreen(state = state, onEvent = { events += it })
            }
        }
    }

    @Test
    fun showsAppNameAsTitle() {
        showHome()

        composeTestRule.onNodeWithText(text(R.string.app_name)).assertIsDisplayed()
    }

    @Test
    fun showsTheThreeBigButtons() {
        showHome()

        listOf(R.string.home_photos_to_pdf, R.string.home_scan, R.string.home_my_pdfs).forEach {
            composeTestRule.onNodeWithText(text(it), useUnmergedTree = true).assertIsDisplayed()
        }
    }

    @Test
    fun settingsButtonHasATalkBackLabel() {
        showHome()

        composeTestRule.onNodeWithContentDescription(text(R.string.settings))
            .assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun tappingPhotosToPdfSendsItsEvent() {
        showHome()

        composeTestRule.onNodeWithText(text(R.string.home_photos_to_pdf)).performClick()

        assertEquals(listOf(HomeEvent.ButtonClick(HomeButton.PhotosToPdf)), events)
    }

    @Test
    fun tappingAToolSendsItsEvent() {
        showHome()

        composeTestRule.onNodeWithText(text(R.string.tool_unlock))
            .performScrollTo()
            .performClick()

        assertEquals(listOf(HomeEvent.ButtonClick(HomeButton.Unlock)), events)
    }

    @Test
    fun showsComingSoonNoteThenSaysItWasShown() {
        composeTestRule.mainClock.autoAdvance = false
        showHome(HomeUiState(showComingSoon = true))

        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.onNodeWithText(text(R.string.coming_soon)).assertIsDisplayed()

        // A short Snackbar stays for 4 seconds.
        composeTestRule.mainClock.advanceTimeBy(10_000)
        assertTrue(HomeEvent.ComingSoonShown in events)
    }
}
