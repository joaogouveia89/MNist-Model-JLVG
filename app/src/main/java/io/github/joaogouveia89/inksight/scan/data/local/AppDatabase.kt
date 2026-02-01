package io.github.joaogouveia89.inksight.scan.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [InferenceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun inferenceDao(): InferenceDao
}
