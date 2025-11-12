package io.github.joaogouveia89.mnistmodelapp.scan.ui.components
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.guava.await

@Composable
fun CameraLifecycleManager(
    preview: Preview,
    imageAnalyzer: ImageAnalysis,
    cameraSelector: CameraSelector
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner) {
        try {

            val cameraProvider = ProcessCameraProvider.getInstance(context).await()

            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            // In case of an error during camera initialization
            // Appropriate log or communication with ViewModel via callback
            // For now, fail silently to avoid crashing the app
        }
    }

    // DisposableEffect for cleanup when removed from the composition.
    DisposableEffect(context) {
        onDispose {
            // Try to unbind it safely.
            // Use runCatching to prevent crashing if CameraProvider has not been initialized.
            runCatching {
                ProcessCameraProvider.getInstance(context).get()?.unbindAll()
            }
        }
    }
}