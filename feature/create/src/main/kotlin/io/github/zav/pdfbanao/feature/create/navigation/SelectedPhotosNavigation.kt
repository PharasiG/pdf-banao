package io.github.zav.pdfbanao.feature.create.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.zav.pdfbanao.feature.create.photos.SelectedPhotosRoute
import kotlinx.serialization.Serializable

/**
 * Navigation address of the Selected photos screen.
 *
 * @param photoUris the picked photos' addresses (content URIs), in page order. Navigation keeps
 *   them, so they come back if Android closes the app in the background and the user returns.
 */
@Serializable
data class SelectedPhotosDestination(
    val photoUris: List<String>,
)

/** Opens the Selected photos screen with these photos. */
fun NavController.navigateToSelectedPhotos(photoUris: List<String>) {
    navigate(SelectedPhotosDestination(photoUris))
}

/** Adds the Selected photos screen to the app's navigation map. */
fun NavGraphBuilder.selectedPhotosScreen(onBack: () -> Unit) {
    composable<SelectedPhotosDestination> { backStackEntry ->
        SelectedPhotosRoute(
            photoUris = backStackEntry.toRoute<SelectedPhotosDestination>().photoUris,
            onBack = onBack,
        )
    }
}
