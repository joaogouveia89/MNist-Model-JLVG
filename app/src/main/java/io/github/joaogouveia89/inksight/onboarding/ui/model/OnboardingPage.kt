package io.github.joaogouveia89.inksight.onboarding.ui.model

/**
 * A sealed class or enum is a better approach than hardcoded numbers to manage onboarding pages.
 * This makes it easier to add, remove, or reorder pages in the future.
 */
enum class OnboardingPage {
    WELCOME,
    CAMERA,
    HISTORY,
    READY
}
