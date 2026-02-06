package io.github.joaogouveia89.inksight.onboarding.data.repository

import io.github.joaogouveia89.inksight.onboarding.data.source.OnboardingLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OnboardingRepository {
    val showOnboarding: Flow<Boolean>
    suspend fun setShowOnboarding(show: Boolean)
}

@Singleton
class OnboardingRepositoryImpl @Inject constructor(
    private val localDataSource: OnboardingLocalDataSource
) : OnboardingRepository {
    /**
     * Flow that emits whether the onboarding should be shown.
     */
    override val showOnboarding: Flow<Boolean> = localDataSource.showOnboarding

    /**
     * Updates the onboarding preference.
     * @param show true if onboarding should be shown next time, false otherwise.
     */
    override suspend fun setShowOnboarding(show: Boolean) {
        localDataSource.setShowOnboarding(show)
    }
}
