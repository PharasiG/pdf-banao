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
    /**
     * True when the photo picker should open.
     * [HomeRoute] opens it, then sends [HomeEvent.PhotoPickerOpened].
     */
    val openPhotoPicker: Boolean = false,
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

    /** The photo picker was opened. */
    data object PhotoPickerOpened : HomeEvent
}

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ButtonClick -> onButtonClick(event.button)
            HomeEvent.ComingSoonShown -> _state.update { it.copy(showComingSoon = false) }
            HomeEvent.PhotoPickerOpened -> _state.update { it.copy(openPhotoPicker = false) }
        }
    }

    private fun onButtonClick(button: HomeButton) {
        when (button) {
            HomeButton.PhotosToPdf -> _state.update { it.copy(openPhotoPicker = true) }

            // These screens aren't built yet, so their buttons say "Coming soon".
            // As each screen is built (see docs/ROADMAP.md), its button opens that screen instead.
            else -> _state.update { it.copy(showComingSoon = true) }
        }
    }
}
