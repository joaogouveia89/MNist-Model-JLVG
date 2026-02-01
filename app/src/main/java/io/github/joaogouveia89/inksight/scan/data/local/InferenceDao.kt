package io.github.joaogouveia89.inksight.scan.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface InferenceDao {
    @Insert
    suspend fun insertInference(inference: InferenceEntity)

    @Query("SELECT * FROM inferences ORDER BY timestamp DESC")
    suspend fun getAllInferences(): List<InferenceEntity>
}
