package io.github.zav.pdfbanao.feature.create.home

import org.junit.Assert.assertEquals
import org.junit.Test

class HomeViewModelTest {
    @Test
    fun startsWithDefaultState() {
        val viewModel = HomeViewModel()

        assertEquals(HomeUiState(), viewModel.state.value)
    }
}
