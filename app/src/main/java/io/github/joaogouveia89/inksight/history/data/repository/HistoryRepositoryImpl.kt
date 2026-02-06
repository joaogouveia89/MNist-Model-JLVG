package io.github.joaogouveia89.inksight.history.data.repository

import android.graphics.BitmapFactory
import io.github.joaogouveia89.inksight.core.data.local.InferenceDao
import io.github.joaogouveia89.inksight.history.domain.model.HistoryItem
import io.github.joaogouveia89.inksight.history.domain.repository.HistoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val inferenceDao: InferenceDao
) : HistoryRepository {

    override fun getAllInferences(): Flow<List<HistoryItem>> {
        return inferenceDao.getAllInferences()
            .map { entities ->
                entities.map { entity ->
                    val bitmap = BitmapFactory.decodeFile(entity.imagePath)
                    HistoryItem(
                        predictedNumber = entity.predictedNumber,
                        confidence = entity.confidence,
                        image = bitmap,
                        isCorrect = entity.isCorrect,
                        timestamp = entity.timestamp
                    )
                }
            }
            .flowOn(Dispatchers.IO)
    }
}
