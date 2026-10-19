package io.github.zav.pdfbanao.feature.create.home

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun startsWithDefaultState() {
        val viewModel = HomeViewModel()

        assertEquals(HomeUiState(), viewModel.state.value)
    }

    @Test
    fun photosToPdfOpensThePhotoPickerInsteadOfComingSoon() {
        val viewModel = HomeViewModel()

        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.PhotosToPdf))

        assertTrue(viewModel.state.value.openPhotoPicker)
        assertFalse(viewModel.state.value.showComingSoon)
    }

    @Test
    fun photoPickerIsOpenedOnlyOnce() {
        val viewModel = HomeViewModel()
        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.PhotosToPdf))

        viewModel.onEvent(HomeEvent.PhotoPickerOpened)

        assertFalse(viewModel.state.value.openPhotoPicker)
    }

    @Test
    fun tappingPhotosToPdfAgainOpensThePickerAgain() {
        val viewModel = HomeViewModel()
        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.PhotosToPdf))
        viewModel.onEvent(HomeEvent.PhotoPickerOpened)

        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.PhotosToPdf))

        assertTrue(viewModel.state.value.openPhotoPicker)
    }

    @Test
    fun everyOtherButtonShowsComingSoon() {
        HomeButton.entries.filter { it != HomeButton.PhotosToPdf }.forEach { button ->
            val viewModel = HomeViewModel()

            viewModel.onEvent(HomeEvent.ButtonClick(button))

            assertTrue("$button should show Coming soon", viewModel.state.value.showComingSoon)
            assertFalse("$button should not open the picker", viewModel.state.value.openPhotoPicker)
        }
    }

    @Test
    fun comingSoonGoesAwayAfterItWasShown() {
        val viewModel = HomeViewModel()
        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.Scan))

        viewModel.onEvent(HomeEvent.ComingSoonShown)

        assertFalse(viewModel.state.value.showComingSoon)
    }

    @Test
    fun tappingAgainAfterTheNoteShowsItAgain() {
        val viewModel = HomeViewModel()
        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.Join))
        viewModel.onEvent(HomeEvent.ComingSoonShown)

        viewModel.onEvent(HomeEvent.ButtonClick(HomeButton.Lock))

        assertTrue(viewModel.state.value.showComingSoon)
    }
}
