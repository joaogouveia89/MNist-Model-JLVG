package io.github.joaogouveia89.inksight.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.joaogouveia89.inksight.scan.data.local.AppDatabase
import io.github.joaogouveia89.inksight.scan.data.local.InferenceDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "inksight_db"
        ).build()
    }

    @Provides
    fun provideInferenceDao(database: AppDatabase): InferenceDao {
        return database.inferenceDao()
    }
}
