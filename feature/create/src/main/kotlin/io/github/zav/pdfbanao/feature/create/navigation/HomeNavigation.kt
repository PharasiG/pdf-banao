package io.github.zav.pdfbanao.feature.create.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.zav.pdfbanao.feature.create.home.HomeRoute
import kotlinx.serialization.Serializable

/** Navigation address of the Home screen. */
@Serializable
data object HomeDestination

/**
 * Adds the Home screen to the app's navigation map.
 *
 * @param onPhotosPick called with the photos the user picked after tapping "Photos to PDF".
 */
fun NavGraphBuilder.homeScreen(onPhotosPick: (photoUris: List<String>) -> Unit) {
    composable<HomeDestination> {
        HomeRoute(onPhotosPick = onPhotosPick)
    }
}
