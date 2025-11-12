package io.github.joaogouveia89.mnistmodelapp.core.ui.preview

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus

@ExperimentalPermissionsApi
class PermissionStatePreview(
    override val permission: String,
    override val status: PermissionStatus
) : PermissionState {
    override fun launchPermissionRequest() { /* NO-OP */
    }
}