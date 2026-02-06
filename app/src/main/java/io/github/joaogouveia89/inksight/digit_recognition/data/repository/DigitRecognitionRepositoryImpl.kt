package io.github.joaogouveia89.inksight.digit_recognition.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.joaogouveia89.inksight.history.ui.HistoryItem
import io.github.joaogouveia89.inksight.digit_recognition.data.local.InferenceDao
import io.github.joaogouveia89.inksight.digit_recognition.data.local.InferenceEntity
import io.github.joaogouveia89.inksight.digit_recognition.domain.CharacterPrediction
import io.github.joaogouveia89.inksight.digit_recognition.domain.repository.DigitRecognitionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import androidx.core.graphics.createBitmap

class DigitRecognitionRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val inferenceDao: InferenceDao
) : DigitRecognitionRepository {

    override suspend fun saveInference(prediction: CharacterPrediction, isCorrect: Boolean) {
        withContext(Dispatchers.IO) {
            val originalBitmap = prediction.frame ?: return@withContext
            
            // Convert ALPHA_8 to ARGB_8888 for PNG compatibility
            val bitmapToSave = if (originalBitmap.config == Bitmap.Config.ALPHA_8) {
                convertToArgb8888(originalBitmap)
            } else {
                originalBitmap
            }

            val fileName = "inference_${UUID.randomUUID()}.png"
            val file = File(context.filesDir, fileName)

            try {
                FileOutputStream(file).use { out ->
                    val success = bitmapToSave.compress(Bitmap.CompressFormat.PNG, 100, out)
                    if (success) {
                        val entity = InferenceEntity(
                            predictedNumber = prediction.number,
                            confidence = prediction.confidence,
                            imagePath = file.absolutePath,
                            isCorrect = isCorrect
                        )
                        inferenceDao.insertInference(entity)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (bitmapToSave != originalBitmap) {
                    bitmapToSave.recycle()
                }
            }
        }
    }

    override fun getAllInferences(): Flow<List<HistoryItem>> {
        return inferenceDao.getAllInferences()
            .map { entities ->
                entities.map { entity ->
                    val bitmap = BitmapFactory.decodeFile(entity.imagePath)
                    HistoryItem(
                        prediction = CharacterPrediction(
                            number = entity.predictedNumber,
                            confidence = entity.confidence,
                            frame = bitmap
                        ),
                        isCorrect = entity.isCorrect,
                        timestamp = entity.timestamp
                    )
                }
            }
            .flowOn(Dispatchers.IO) // Ensure decoding and mapping happens on IO thread
    }

    private fun convertToArgb8888(alpha8Bitmap: Bitmap): Bitmap {
        val argbBitmap = createBitmap(alpha8Bitmap.width, alpha8Bitmap.height)
        val canvas = Canvas(argbBitmap)
        val paint = Paint()
        canvas.drawBitmap(alpha8Bitmap, 0f, 0f, paint)
        return argbBitmap
    }
}
