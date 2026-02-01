package io.github.joaogouveia89.inksight.scan.domain.repository

import io.github.joaogouveia89.inksight.scan.domain.CharacterPrediction

interface InferenceRepository {
    suspend fun saveInference(prediction: CharacterPrediction, isCorrect: Boolean)
}
