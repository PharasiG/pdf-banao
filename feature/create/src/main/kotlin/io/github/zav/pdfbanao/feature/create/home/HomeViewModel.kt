package io.github.zav.pdfbanao.feature.create.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * Everything the Home screen shows.
 * Task 0.3 adds the big buttons and task 3.1 adds "Recent PDFs".
 */
data class HomeUiState(
    val isLoading: Boolean = false,
)

/** Everything the user can do on the Home screen. None yet: task 0.3 adds the buttons. */
sealed interface HomeEvent

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        // Nothing to handle yet. Task 0.3 adds the first events.
    }
}
