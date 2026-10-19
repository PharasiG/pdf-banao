package io.github.zav.pdfbanao.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import io.github.zav.pdfbanao.feature.create.navigation.HomeDestination
import io.github.zav.pdfbanao.feature.create.navigation.homeScreen
import io.github.zav.pdfbanao.feature.create.navigation.navigateToSelectedPhotos
import io.github.zav.pdfbanao.feature.create.navigation.selectedPhotosScreen

/** The app's navigation map. Every screen of every feature is added here. */
@Composable
fun PdfBanaoNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = HomeDestination,
        modifier = modifier,
    ) {
        homeScreen(onPhotosPick = navController::navigateToSelectedPhotos)
        selectedPhotosScreen(onBack = navController::popBackStack)
    }
}
