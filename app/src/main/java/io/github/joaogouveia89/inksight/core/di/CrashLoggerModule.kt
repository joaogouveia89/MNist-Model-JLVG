package io.github.joaogouveia89.inksight.core.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.joaogouveia89.inksight.core.data.FirebaseCrashLogger
import io.github.joaogouveia89.inksight.core.domain.CrashLogger
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CrashLoggerModule {

    @Binds
    @Singleton
    abstract fun bindCrashLogger(
        firebaseCrashLogger: FirebaseCrashLogger
    ): CrashLogger
}
