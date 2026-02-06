package io.github.joaogouveia89.inksight.history.ui

import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction

data class HistoryItem(
    val prediction: CharacterPrediction,
    val isCorrect: Boolean,
    val timestamp: Long
)

data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<HistoryItem> = emptyList(),
    val accuracyRate: Float = 0f
)
