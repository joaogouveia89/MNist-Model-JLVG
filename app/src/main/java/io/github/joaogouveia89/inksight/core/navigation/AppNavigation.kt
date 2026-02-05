package io.github.joaogouveia89.inksight.core.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.joaogouveia89.inksight.core.data.local.OnboardingPreferences
import io.github.joaogouveia89.inksight.core.ui.components.OnboardingScreen
import io.github.joaogouveia89.inksight.history.ui.HistoryScreen
import io.github.joaogouveia89.inksight.history.ui.HistoryViewModel
import io.github.joaogouveia89.inksight.scan.ui.ScanCommand
import io.github.joaogouveia89.inksight.scan.ui.ScanScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanViewModel
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    onboardingPreferences: OnboardingPreferences // Idealmente injetado via ViewModel, mas para simplicidade aqui
) {
    val navController = rememberNavController()
    val showOnboarding by onboardingPreferences.showOnboarding.collectAsState(initial = null)
    val scope = rememberCoroutineScope()

    if (showOnboarding == null) return // Aguarda carregar as preferências

    NavHost(
        navController = navController,
        startDestination = if (showOnboarding == true) NavRoute.Onboarding else NavRoute.Scan,
        modifier = modifier
    ) {
        composable<NavRoute.Onboarding> {
            OnboardingScreen(
                onFinish = { showAgain ->
                    scope.launch {
                        onboardingPreferences.setShowOnboarding(showAgain)
                        navController.navigate(NavRoute.Scan) {
                            popUpTo(NavRoute.Onboarding) { inclusive = true }
                        }
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
