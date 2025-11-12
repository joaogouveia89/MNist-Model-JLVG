package io.github.joaogouveia89.mnistmodelapp.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.shouldShowRationale
import io.github.joaogouveia89.mnistmodelapp.R
import io.github.joaogouveia89.mnistmodelapp.core.ui.preview.PermissionStatePreview
import io.github.joaogouveia89.mnistmodelapp.core.ui.theme.Dimens
import io.github.joaogouveia89.mnistmodelapp.core.ui.theme.MNistModelAppTheme

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraPermissionScreen(
    cameraPermissionState: PermissionState,
    modifier: Modifier = Modifier
) {
    val textToShow = if (cameraPermissionState.status.shouldShowRationale)
        R.string.camera_permission_message_shouldShowRationale
    else R.string.camera_permission_message_general

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = Dimens.screenPaddingHorizontal,
                vertical = Dimens.screenPaddingVertical
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = "Camera icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(Dimens.iconSizeLarge)
        )

        Spacer(modifier = Modifier.height(Dimens.contentSpacingLarge))

        Text(
            text = stringResource(textToShow),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(Dimens.contentSpacingLarge))

        Button(
            onClick = { cameraPermissionState.launchPermissionRequest() },
            shape = RoundedCornerShape(Dimens.buttonCornerRadius),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight)
        ) {
            Icon(
                imageVector = Icons.Default.Videocam,
                contentDescription = null,
                modifier = Modifier.padding(end = Dimens.iconPaddingEnd)
            )
            Text(stringResource(R.string.allow_access_to_the_camera))
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
private fun CameraPermissionScreenPreview() {
    MNistModelAppTheme {
        CameraPermissionScreen(
            cameraPermissionState = PermissionStatePreview(
                permission = "camera",
                status = PermissionStatus.Denied(false)
            ),
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Preview(showBackground = true)
@Composable
private fun CameraPermissionScreenShowRationalePreview() {
    MNistModelAppTheme {
        CameraPermissionScreen(
            cameraPermissionState = PermissionStatePreview(
                permission = "camera",
                status = PermissionStatus.Denied(true)
            ),
        )
    }
}