package io.github.joaogouveia89.inksight.core.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface InferenceDao {
    @Insert
    suspend fun insertInference(inference: InferenceEntity)

    @Query("SELECT * FROM inferences ORDER BY timestamp DESC")
    fun getAllInferences(): Flow<List<InferenceEntity>>
}
