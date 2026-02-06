package io.github.joaogouveia89.inksight.digit_recognition.domain.repository

import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction

interface DigitRecognitionRepository {
    suspend fun saveInference(prediction: CharacterPrediction, isCorrect: Boolean)
}
