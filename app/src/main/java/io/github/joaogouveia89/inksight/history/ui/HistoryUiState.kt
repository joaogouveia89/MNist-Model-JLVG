package io.github.joaogouveia89.inksight.history.ui

import io.github.joaogouveia89.inksight.history.domain.model.HistoryItem

data class HistoryUiState(
    val isLoading: Boolean = false,
    val items: List<HistoryItem> = emptyList(),
    val accuracyRate: Float = 0f
)
