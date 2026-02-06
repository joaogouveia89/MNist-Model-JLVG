package io.github.joaogouveia89.inksight.history.domain.repository

import io.github.joaogouveia89.inksight.history.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getAllInferences(): Flow<List<HistoryItem>>
}
