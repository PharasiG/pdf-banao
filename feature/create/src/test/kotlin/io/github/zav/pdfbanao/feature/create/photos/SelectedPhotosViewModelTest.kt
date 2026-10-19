package io.github.zav.pdfbanao.feature.create.photos

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SelectedPhotosViewModelTest {
    private fun photos(count: Int) = List(count) { "content://media/picker/0/photo$it" }

    @Test
    fun showsThePhotosInTheOrderTheyWerePicked() {
        val picked = listOf("content://photo/c", "content://photo/a", "content://photo/b")

        val viewModel = SelectedPhotosViewModel(picked)

        assertEquals(picked, viewModel.state.value.photoUris)
        assertFalse(viewModel.state.value.someLeftOut)
    }

    @Test
    fun keepsAllPhotosUpToTheLimit() {
        val viewModel = SelectedPhotosViewModel(photos(MAX_PHOTOS))

        assertEquals(photos(MAX_PHOTOS), viewModel.state.value.photoUris)
        assertFalse(viewModel.state.value.someLeftOut)
    }

    @Test
    fun keepsOnlyTheFirstPhotosOverTheLimitAndSaysSo() {
        val viewModel = SelectedPhotosViewModel(photos(MAX_PHOTOS + 5))

        assertEquals(photos(MAX_PHOTOS), viewModel.state.value.photoUris)
        assertTrue(viewModel.state.value.someLeftOut)
    }

    @Test
    fun limitIsOneHundredPhotos() {
        // PRD F1, and the most the Android photo picker allows.
        assertEquals(100, MAX_PHOTOS)
    }
}
