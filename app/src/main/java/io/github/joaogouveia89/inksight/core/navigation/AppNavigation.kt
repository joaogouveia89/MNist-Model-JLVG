package io.github.joaogouveia89.inksight.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.joaogouveia89.inksight.history.ui.HistoryScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanViewModel

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoute.Scan,
        modifier = modifier
    ) {
        composable<NavRoute.Scan> {
            val viewModel: ScanViewModel = hiltViewModel()
            ScanScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(NavRoute.History) }
            )
        }
        composable<NavRoute.History> {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
