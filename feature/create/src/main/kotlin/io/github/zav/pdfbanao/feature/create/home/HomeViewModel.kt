package io.github.zav.pdfbanao.feature.create.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Everything the Home screen shows.
 * Task 3.1 adds the list of recent PDFs.
 */
data class HomeUiState(
    /** True while the "Coming soon" note is on screen. */
    val showComingSoon: Boolean = false,
)

/** Every button on the Home screen. */
enum class HomeButton {
    PhotosToPdf,
    Scan,
    MyPdfs,
    IdCard,
    MakeSmaller,
    Join,
    TakeOutPages,
    ArrangePages,
    PdfToPhotos,
    Lock,
    Unlock,
    Settings,
}

/** Everything the user can do on the Home screen. */
sealed interface HomeEvent {
    /** The user tapped one of the buttons. */
    data class ButtonClick(
        val button: HomeButton,
    ) : HomeEvent

    /** The "Coming soon" note was shown and has gone away. */
    data object ComingSoonShown : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            // No button has its screen yet, so every button says "Coming soon".
            // As each screen is built (see docs/ROADMAP.md), its button opens that screen instead.
            is HomeEvent.ButtonClick -> _state.update { it.copy(showComingSoon = true) }

            HomeEvent.ComingSoonShown -> _state.update { it.copy(showComingSoon = false) }
        }
    }
}
