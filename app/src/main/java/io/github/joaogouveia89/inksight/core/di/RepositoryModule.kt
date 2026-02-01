package io.github.joaogouveia89.inksight.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.joaogouveia89.inksight.scan.data.repository.InferenceRepositoryImpl
import io.github.joaogouveia89.inksight.scan.domain.repository.InferenceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindInferenceRepository(
        inferenceRepositoryImpl: InferenceRepositoryImpl
    ): InferenceRepository
}
