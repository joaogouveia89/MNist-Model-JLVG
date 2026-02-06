package io.github.joaogouveia89.inksight.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.joaogouveia89.inksight.onboarding.data.repository.OnboardingRepository
import io.github.joaogouveia89.inksight.onboarding.data.repository.OnboardingRepositoryImpl
import io.github.joaogouveia89.inksight.digit_recognition.data.repository.DigitRecognitionRepositoryImpl
import io.github.joaogouveia89.inksight.digit_recognition.domain.repository.DigitRecognitionRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInferenceRepository(
        inferenceRepositoryImpl: DigitRecognitionRepositoryImpl
    ): DigitRecognitionRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        onboardingRepositoryImpl: OnboardingRepositoryImpl
    ): OnboardingRepository
}
