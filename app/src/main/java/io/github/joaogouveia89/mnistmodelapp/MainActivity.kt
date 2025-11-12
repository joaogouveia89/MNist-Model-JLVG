package io.github.joaogouveia89.mnistmodelapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import io.github.joaogouveia89.mnistmodelapp.core.ui.theme.MNistModelAppTheme
import io.github.joaogouveia89.mnistmodelapp.scan.ui.ScanScreen
import io.github.joaogouveia89.mnistmodelapp.scan.ui.ScanViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MNistModelAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: ScanViewModel by viewModels {
                        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
                    }
                    ScanScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel
                    )
                }
            }
        }
    }
}