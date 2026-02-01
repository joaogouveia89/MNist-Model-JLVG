package io.github.joaogouveia89.inksight.scan.domain.repository

import io.github.joaogouveia89.inksight.history.ui.HistoryItem
import io.github.joaogouveia89.inksight.scan.domain.CharacterPrediction
import kotlinx.coroutines.flow.Flow

interface InferenceRepository {
    suspend fun saveInference(prediction: CharacterPrediction, isCorrect: Boolean)
    fun getAllInferences(): Flow<List<HistoryItem>>
}
