package io.github.joaogouveia89.inksight.core.navigation

import kotlinx.serialization.Serializable

sealed interface NavRoute {
    @Serializable
    data object Onboarding : NavRoute

    @Serializable
    data object Scan : NavRoute

    @Serializable
    data object History : NavRoute
}
