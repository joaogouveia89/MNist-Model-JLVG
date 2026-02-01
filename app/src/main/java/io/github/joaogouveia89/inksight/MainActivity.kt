package io.github.joaogouveia89.inksight

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.joaogouveia89.inksight.core.ui.theme.MNistModelAppTheme
import io.github.joaogouveia89.inksight.history.ui.HistoryScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanScreen
import io.github.joaogouveia89.inksight.scan.ui.ScanViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MNistModelAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun AppNavigation(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "scan",
        modifier = modifier
    ) {
        composable("scan") {
            val viewModel: ScanViewModel = hiltViewModel()
            ScanScreen(
                viewModel = viewModel,
                onNavigateToHistory = { navController.navigate("history") }
            )
        }
        composable("history") {
            HistoryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
