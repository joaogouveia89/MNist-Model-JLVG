package io.github.joaogouveia89.inksight.core.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.joaogouveia89.inksight.history.ui.HistoryScreen
import io.github.joaogouveia89.inksight.history.ui.HistoryViewModel
import io.github.joaogouveia89.inksight.onboarding.ui.OnboardingScreen
import io.github.joaogouveia89.inksight.onboarding.ui.OnboardingViewModel
import io.github.joaogouveia89.inksight.scan.ui.ScanCommand
import io.github.joaogouveia89.inksight.scan.ui.ScanScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val onboardingViewModel: OnboardingViewModel = hiltViewModel()
    val showOnboarding by onboardingViewModel.showOnboarding.collectAsStateWithLifecycle()

    // Wait for the preferences to load before rendering the NavHost
    if (showOnboarding == null) return

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding == true) NavRoute.Onboarding else NavRoute.Scan,
        modifier = modifier
    ) {
        composable<NavRoute.Onboarding> {
            OnboardingScreen(
                onFinish = { showAgain ->
                    onboardingViewModel.completeOnboarding(showAgain)
                    navController.navigate(NavRoute.Scan) {
                        popUpTo(NavRoute.Onboarding) { inclusive = true }
                    }
                }
            )
        }
        composable<NavRoute.Scan> {
            val viewModel: ScanViewModel = hiltViewModel()

            DisposableEffect(Unit) {
                viewModel.execute(ScanCommand.OnStartScanning)
                onDispose {
                    viewModel.execute(ScanCommand.OnStopScanning)
                }
            }

            ScanScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate(NavRoute.History) }
            )
        }
        composable<NavRoute.History> {
            val historyViewModel: HistoryViewModel = hiltViewModel()
            val historyState by historyViewModel.uiState.collectAsStateWithLifecycle()

            HistoryScreen(
                state = historyState,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
