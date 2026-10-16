package io.github.zav.pdfbanao.feature.create.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.zav.pdfbanao.feature.create.home.HomeRoute
import kotlinx.serialization.Serializable

/** Navigation address of the Home screen. */
@Serializable
data object HomeDestination

/** Adds the Home screen to the app's navigation map. */
fun NavGraphBuilder.homeScreen() {
    composable<HomeDestination> {
        HomeRoute()
    }
}
