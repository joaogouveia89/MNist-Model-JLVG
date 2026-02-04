package io.github.joaogouveia89.inksight.scan.ui.components

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.guava.await

/**
 * Manages the CameraX lifecycle within a Composable.
 * It ensures that the camera is bound/unbound correctly as the lifecycle changes.
 */
@Composable
fun CameraLifecycleManager(
    preview: Preview,
    imageAnalyzer: ImageAnalysis,
    cameraSelector: CameraSelector,
    onError: (String) -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current

    LaunchedEffect(lifecycleOwner, cameraSelector, preview, imageAnalyzer) {
        try {
            val cameraProvider = ProcessCameraProvider.getInstance(context).await()

            // Always unbind everything before rebinding to prevent "Camera already bound" exceptions
            // during rapid navigation or configuration changes.
            cameraProvider.unbindAll()

            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (e: Exception) {
            // If the coroutine was cancelled (e.g., navigated away during initialization),
            // we should not report an error as it's expected behavior.
            if (e is CancellationException) throw e
            
            onError("Failed to initialize camera: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    // Note: Manual unbindAll() in DisposableEffect is removed.
    // bindToLifecycle automatically unbinds when the lifecycleOwner (LocalLifecycleOwner) 
    // is destroyed or removed from the composition, which is safer for a Singleton provider.
}
