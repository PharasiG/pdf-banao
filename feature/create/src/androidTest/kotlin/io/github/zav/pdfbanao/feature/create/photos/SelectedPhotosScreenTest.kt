package io.github.zav.pdfbanao.feature.create.photos

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import io.github.zav.pdfbanao.core.ui.R
import io.github.zav.pdfbanao.core.ui.theme.PdfBanaoTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectedPhotosScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val events = mutableListOf<SelectedPhotosEvent>()

    private val context: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    private fun page(number: Int) = context.getString(R.string.page_number, number)

    private fun title(count: Int) =
        context.resources.getQuantityString(R.plurals.selected_photos_title, count, count)

    private fun leftOutNote() = context.resources.getQuantityString(
        R.plurals.selected_photos_left_out,
        MAX_PHOTOS,
        MAX_PHOTOS,
    )

    // Not real photos, so the pictures stay empty boxes. The tests check everything else.
    private fun photos(count: Int) = List(count) { "content://test/photo$it" }

    private fun showScreen(state: SelectedPhotosUiState) {
        composeTestRule.setContent {
            PdfBanaoTheme {
                SelectedPhotosScreen(state = state, onEvent = { events += it })
            }
        }
    }

    @Test
    fun titleSaysHowManyPhotosWerePicked() {
        showScreen(SelectedPhotosUiState(photoUris = photos(3)))

        composeTestRule.onNodeWithText(title(3)).assertIsDisplayed()
    }

    @Test
    fun titleUsesOnePhotoForASinglePhoto() {
        showScreen(SelectedPhotosUiState(photoUris = photos(1)))

        composeTestRule.onNodeWithText(title(1)).assertIsDisplayed()
    }

    @Test
    fun showsOneNumberedRowPerPhoto() {
        showScreen(SelectedPhotosUiState(photoUris = photos(3)))

        (1..3).forEach { composeTestRule.onNodeWithText(page(it)).assertIsDisplayed() }
        composeTestRule.onNodeWithText(page(4)).assertDoesNotExist()
    }

    @Test
    fun longListScrollsToTheLastPhoto() {
        showScreen(SelectedPhotosUiState(photoUris = photos(MAX_PHOTOS)))

        composeTestRule.onNode(hasScrollAction()).performScrollToNode(hasText(page(MAX_PHOTOS)))

        composeTestRule.onNodeWithText(page(MAX_PHOTOS)).assertIsDisplayed()
    }

    @Test
    fun backArrowHasATalkBackLabelAndSendsBackClick() {
        showScreen(SelectedPhotosUiState(photoUris = photos(2)))

        composeTestRule.onNodeWithContentDescription(context.getString(R.string.back))
            .assertIsDisplayed()
            .assertHasClickAction()
            .performClick()

        assertEquals(listOf(SelectedPhotosEvent.BackClick), events)
    }

    @Test
    fun saysWhenSomePhotosWereLeftOut() {
        showScreen(SelectedPhotosUiState(photoUris = photos(MAX_PHOTOS), someLeftOut = true))

        val note = leftOutNote()
        composeTestRule.onNodeWithText(note).assertIsDisplayed()
    }

    @Test
    fun noNoteWhenNoPhotosWereLeftOut() {
        showScreen(SelectedPhotosUiState(photoUris = photos(2)))

        val note = leftOutNote()
        composeTestRule.onNodeWithText(note).assertDoesNotExist()
    }
}
