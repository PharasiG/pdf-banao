package io.github.zav.pdfbanao.feature.create.photos

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Everything the Selected photos screen shows.
 * Task 1.3 adds making the PDF; task 2.1 turns this list into the Arrange pages screen.
 */
data class SelectedPhotosUiState(
    /** The photos' addresses (content URIs), in page order. At most [MAX_PHOTOS]. */
    val photoUris: List<String> = emptyList(),
    /** True if more than [MAX_PHOTOS] photos came in, so only the first [MAX_PHOTOS] were kept. */
    val someLeftOut: Boolean = false,
)

/** Everything the user can do on the Selected photos screen. */
sealed interface SelectedPhotosEvent {
    /** The user tapped the back arrow. [SelectedPhotosRoute] goes back, the ViewModel isn't told. */
    data object BackClick : SelectedPhotosEvent
}

/**
 * Holds the photos the user picked.
 *
 * Hilt gives it [photoUris] when it is created ("assisted injection"): [SelectedPhotosRoute] passes
 * them in from the navigation route. Tests can simply call the constructor with a list.
 */
@HiltViewModel(assistedFactory = SelectedPhotosViewModel.Factory::class)
class SelectedPhotosViewModel @AssistedInject constructor(
    @Assisted photoUris: List<String>,
) : ViewModel() {
    private val _state = MutableStateFlow(
        SelectedPhotosUiState(
            // The newer photo pickers stop at MAX_PHOTOS. Older phones fall back to Android's file
            // picker, which has no limit, so keep the first MAX_PHOTOS and tell the user.
            photoUris = photoUris.take(MAX_PHOTOS),
            someLeftOut = photoUris.size > MAX_PHOTOS,
        ),
    )
    val state: StateFlow<SelectedPhotosUiState> = _state.asStateFlow()

    @AssistedFactory
    interface Factory {
        fun create(photoUris: List<String>): SelectedPhotosViewModel
    }
}
