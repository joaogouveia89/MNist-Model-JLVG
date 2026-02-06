package io.github.joaogouveia89.inksight.core.navigation

import kotlinx.serialization.Serializable

sealed interface NavRoute {
    @Serializable
    data object Onboarding : NavRoute

    @Serializable
    data object DigitRecognition : NavRoute

    @Serializable
    data object History : NavRoute
}
