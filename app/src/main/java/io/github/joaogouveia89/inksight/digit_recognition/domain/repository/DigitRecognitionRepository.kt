package io.github.joaogouveia89.inksight.digit_recognition.domain.repository

import io.github.joaogouveia89.inksight.history.ui.HistoryItem
import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction
import kotlinx.coroutines.flow.Flow

interface DigitRecognitionRepository {
    suspend fun saveInference(prediction: CharacterPrediction, isCorrect: Boolean)
    fun getAllInferences(): Flow<List<HistoryItem>>
}
