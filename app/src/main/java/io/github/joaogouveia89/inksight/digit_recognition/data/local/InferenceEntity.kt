package io.github.joaogouveia89.inksight.digit_recognition.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inferences")
data class InferenceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val predictedNumber: Int,
    val confidence: Int,
    val imagePath: String,
    val isCorrect: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
